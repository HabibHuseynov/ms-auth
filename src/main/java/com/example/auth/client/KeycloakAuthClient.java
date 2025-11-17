package com.example.auth.client;

import com.example.auth.model.request.IntrospectRequest;
import com.example.auth.model.request.PermissionRequest;
import com.example.auth.model.request.RptRequest;
import jakarta.ws.rs.core.Response;
import com.example.auth.model.response.UserInfoResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "keycloak-auth-client",
        url = "${keycloak.auth-server-url}",
        configuration = KeycloakFeignConfig.class
)
public interface KeycloakAuthClient {

    @PostMapping("/realms/${keycloak.realm}/protocol/openid-connect/token/introspect")
    Map<String, Object> introspectToken(@RequestBody IntrospectRequest request);

    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE})
    Map<String, Object> checkPermission(
            @RequestBody PermissionRequest request);

    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Map<String, Object> getRpt(@RequestBody Map<String, ?> formParams);

    @GetMapping("/realms/${keycloak.realm}/protocol/openid-connect/userinfo")
    UserInfoResponse getUserInfo();

    @PostMapping(
            value = "/realms/${keycloak.realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    Map<String, Object> exchangeToken(@RequestBody Map<String, ?> formParams);

    @PostMapping(
            value = "/admin/realms/{realm}/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    Response createUser(
            @PathVariable("realm") String realm,
            @RequestBody UserRepresentation user
    );

    @PutMapping(
            value = "/admin/realms/{realm}/users/{userId}/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void resetPassword(
            @PathVariable("realm") String realm,
            @PathVariable("userId") String userId,
            @RequestBody CredentialRepresentation password
    );

}
