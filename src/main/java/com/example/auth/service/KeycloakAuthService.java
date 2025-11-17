package com.example.auth.service;

import com.example.auth.client.KeycloakAdminFeignClient;
import com.example.auth.client.KeycloakAuthClient;
import com.example.auth.model.request.LoginRequest;
import com.example.auth.model.request.RefreshRequest;
import com.example.auth.model.request.RegisterRequest;
import com.example.auth.model.response.LoginResponse;
import com.example.auth.model.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import com.example.auth.model.request.PasswordCredential;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAuthService {

    private final KeycloakAuthClient authClient;
    private final KeycloakAdminFeignClient adminClient;

    private final String REALM = "myrealm";
    private final String USER_CLIENT_ID = "auth_resource";
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public void registerUser(RegisterRequest req) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setEnabled(true);

        ResponseEntity<Void> response = adminClient.createUser(REALM, user);
        String location = response.getHeaders().getFirst("Location");
        if (location == null || location.isBlank()) {
            throw new IllegalStateException("Keycloak createUser missing Location header");
        }
        String userId = extractIdFromLocation(location);
        PasswordCredential credential = new PasswordCredential("password", req.password(), false);
        adminClient.resetPassword(REALM, userId, credential);

        var roleRepresentation = adminClient.getRealmRole(REALM, "user");
        Set<String> rolesToAssign = new HashSet<>();

        adminClient.addRealmRolesToUser(REALM, userId, List.of(roleRepresentation));

        log.info("User {} registered with roles: {}", req.username(), rolesToAssign);
    }

    public LoginResponse login(LoginRequest req) {

        Map<String, Object> formParams = new HashMap<>();
        formParams.put("grant_type", "password");
        formParams.put("client_id", USER_CLIENT_ID);
        formParams.put("client_secret", clientSecret);
        formParams.put("username", req.username());
        formParams.put("password", req.password());
        formParams.put("scope", "openid profile email");
        Map<String, Object> tokenResponse = authClient.exchangeToken(formParams);

        return new LoginResponse(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                ((Number) tokenResponse.get("expires_in")).longValue(),
                (String) tokenResponse.get("token_type")
        );
    }

    public LoginResponse refresh(RefreshRequest req) {
        Map<String, Object> formParams = new HashMap<>();
        formParams.put("grant_type", "refresh_token");
        formParams.put("client_id", USER_CLIENT_ID);
        formParams.put("client_secret", clientSecret);
        formParams.put("refresh_token", req.refreshToken());
        formParams.put("scope", "openid profile email");
        Map<String, Object> tokenResponse = authClient.exchangeToken(formParams);

        return new LoginResponse(
                (String) tokenResponse.get("access_token"),
                (String) tokenResponse.get("refresh_token"),
                ((Number) tokenResponse.get("expires_in")).longValue(),
                (String) tokenResponse.get("token_type")
        );
    }

    public UserInfoResponse getUserInfo() {
        UserInfoResponse info = authClient.getUserInfo();

        List<String> roles = List.of();
        if (info.sub() != null && !info.sub().isBlank()) {
            List<RoleRepresentation> effectiveRoles = adminClient.getEffectiveRealmRoles(REALM, info.sub());
            roles = effectiveRoles == null ? List.of() : effectiveRoles.stream()
                        .map(RoleRepresentation::getName)
                        .filter(Objects::nonNull)
                        .toList();
        }
        return new UserInfoResponse(info.sub(),
                info.name(),
                info.preferredUsername(),
                info.givenName(),
                info.familyName(),
                info.email(),
                info.emailVerified(),
                roles);
    }

    private String extractIdFromLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Empty Location header");
        }
        int idx = location.lastIndexOf('/');
        if (idx < 0 || idx == location.length() - 1) {
            throw new IllegalStateException("Cannot extract userId from Location: " + location);
        }
        return location.substring(idx + 1);
    }
}