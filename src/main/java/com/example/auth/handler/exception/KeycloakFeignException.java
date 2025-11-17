package com.example.auth.handler.exception;

import feign.Response;
import lombok.Getter;

@Getter
public class KeycloakFeignException extends RuntimeException {
    public final Response response;
    public final String keycloakBody;

    public KeycloakFeignException(String message, Response response, String body) {
        super(message);
        this.response = response;
        this.keycloakBody = body;
    }
}


