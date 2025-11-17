package com.example.auth.model.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {}
