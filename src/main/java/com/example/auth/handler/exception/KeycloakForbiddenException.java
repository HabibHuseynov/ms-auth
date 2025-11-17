package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakForbiddenException extends KeycloakFeignException {
    public KeycloakForbiddenException(String msg, Response r, String body) { super(msg, r, body); }
}
