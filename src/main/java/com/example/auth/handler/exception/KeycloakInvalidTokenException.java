package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakInvalidTokenException extends KeycloakFeignException {
    public KeycloakInvalidTokenException(String msg, Response r, String body) { super(msg, r, body); }
}
