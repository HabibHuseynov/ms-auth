package com.example.auth.model.request;

import feign.form.FormProperty;

public record TokenRequest(
        @FormProperty("grant_type") String grantType,
        @FormProperty("client_id") String clientId,
        @FormProperty("username") String username,
        @FormProperty("password") String password,
        @FormProperty("refresh_token") String refreshToken
) {
    // For login
    public TokenRequest(String clientId, String username, String password) {
        this("password", clientId, username, password, null);
    }

    // For refresh
    public TokenRequest(String clientId, String refreshToken) {
        this("refresh_token", clientId, null, null, refreshToken);
    }
}
