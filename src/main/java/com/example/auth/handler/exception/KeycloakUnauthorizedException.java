package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakUnauthorizedException extends KeycloakFeignException {
    public KeycloakUnauthorizedException(String msg, Response r, String body) { super(msg, r, body); }
}
