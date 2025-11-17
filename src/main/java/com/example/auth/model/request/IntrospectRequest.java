package com.example.auth.model.request;

public record IntrospectRequest(
        String token,
        String client_id,
        String client_secret
) {}

