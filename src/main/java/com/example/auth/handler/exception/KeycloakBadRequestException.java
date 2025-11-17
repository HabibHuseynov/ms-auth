package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakBadRequestException extends KeycloakFeignException {
    public KeycloakBadRequestException(String msg, Response r, String body) { super(msg, r, body); }
}
