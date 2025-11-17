package com.example.auth.client;

import com.example.auth.handler.exception.*;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class KeycloakErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String requestUrl = response.request().url();
        String method = response.request().httpMethod().name();
        int status = response.status();
        String body = safeReadBody(response);

        log.error("""
                KEYCLOAK FEIGN ERROR
                Time    : 2025-11-15 10:13 AM +04
                Method  : {} {}
                Status  : {} {}
                Body    : {}
                """, method, requestUrl, status, HttpStatus.valueOf(status).getReasonPhrase(), body);

        return switch (status) {
            case 400 -> new KeycloakBadRequestException("Invalid request to Keycloak", response, body);
            case 401 -> new KeycloakUnauthorizedException("Client credentials invalid or token expired", response, body);
            case 403 -> new KeycloakForbiddenException("Forbidden – client not allowed", response, body);
            case 404 -> new KeycloakNotFoundException("Keycloak resource not found: " + requestUrl, response, body);
            case 409 -> new KeycloakFeignException("Conflict – resource already exists or violates constraints", response, body);

            case 500 -> new KeycloakServerErrorException("Keycloak internal server error", response, body);
            case 502, 503, 504 -> new KeycloakUnavailableException("Keycloak is down or unreachable", response, body);

            default -> {
                if (methodKey.contains("checkPermission") && status == 403) {
                    yield new KeycloakPermissionDeniedException("Access denied by policy – user lacks permission", response, body);
                }
                if (body != null && body.contains("invalid_token")) {
                    yield new KeycloakInvalidTokenException("Token is invalid or expired", response, body);
                }
                if (body != null && body.contains("permission")) {
                    yield new KeycloakPermissionDeniedException("UMA ticket rejected – permission not granted", response, body);
                }

                // Fallback
                yield new KeycloakFeignException("Keycloak error " + status, response, body);
            }
        };
    }

    private String safeReadBody(Response response) {
        if (response.body() == null) return "<no body>";
        try (var in = response.body().asInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("[\\r\\n]+", " ")
                    .trim();
        } catch (IOException e) {
            return "<failed to read body: " + e.getMessage() + ">";
        }
    }
}
