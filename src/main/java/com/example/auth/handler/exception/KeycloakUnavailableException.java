package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakUnavailableException extends KeycloakFeignException {
    public KeycloakUnavailableException(String msg, Response r, String body) { super(msg, r, body); }
}
