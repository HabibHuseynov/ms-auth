package com.example.auth.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakAdminConfig {
//    @Value("${keycloak.auth-server-url}")
//    private String serverUrl;
//
//    @Value("${keycloak.admin.realm:master}")
//    private String adminRealm;
//
//    @Value("${keycloak.admin.client-id}")
//    private String adminClientId;
//
//    @Value("${keycloak.admin.client-secret}")
//    private String adminClientSecret;
//
//    @Bean
//    public Keycloak keycloakAdminClient() {
//        return KeycloakBuilder.builder()
//                .serverUrl(serverUrl)
//                .realm(adminRealm)
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .clientId(adminClientId)
//                .clientSecret(adminClientSecret)
//                .build();
//    }
}
