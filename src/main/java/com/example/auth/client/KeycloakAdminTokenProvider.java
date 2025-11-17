package com.example.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminTokenProvider {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    @Value("${keycloak.admin.client-secret}")
    private String adminClientSecret;

    private volatile String cachedToken;
    private volatile long expiresAtEpochSeconds = 0L;

    private final RestTemplate restTemplate = new RestTemplate();

    public synchronized String getToken() {
        long now = Instant.now().getEpochSecond();
        if (cachedToken != null && now < expiresAtEpochSeconds - 10) {
            return cachedToken;
        }

        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", adminClientId);
        form.add("client_secret", adminClientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        Map<?, ?> resp = restTemplate.postForObject(url, entity, Map.class);
        if (resp == null || resp.get("access_token") == null) {
            throw new IllegalStateException("Failed to obtain admin access token from Keycloak");
        }

        cachedToken = String.valueOf(resp.get("access_token"));
        Object expiresObj = resp.get("expires_in");
        long expiresIn = (expiresObj instanceof Number) ? ((Number) expiresObj).longValue() : 60L;
        expiresAtEpochSeconds = now + expiresIn;
        return cachedToken;
    }
}
