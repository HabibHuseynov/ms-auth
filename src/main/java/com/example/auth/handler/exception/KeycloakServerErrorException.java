package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakServerErrorException extends KeycloakFeignException {
    public KeycloakServerErrorException(String msg, Response r, String body) { super(msg, r, body); }
}
