package com.example.auth.handler.exception;

import feign.Response;

public class KeycloakPermissionDeniedException extends KeycloakFeignException {
    public KeycloakPermissionDeniedException(String msg, Response r, String body) { super(msg, r, body); }
}
