package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakNotFoundException extends KeycloakFeignException {
    public KeycloakNotFoundException(String msg, Response r, String body) { super(msg, r, body); }
}
