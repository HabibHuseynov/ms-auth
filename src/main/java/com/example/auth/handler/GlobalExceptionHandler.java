package com.example.auth.handler;

import com.example.auth.handler.exception.KeycloakFeignException;
import com.example.auth.handler.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // ────────────────────── KEYCLOACK ERRORS ──────────────────────
    @ExceptionHandler(KeycloakFeignException.class)
    public ResponseEntity<ErrorResponse> handleKeycloakError(
            KeycloakFeignException ex,
            HttpServletRequest request) {

        logStructuredError(request,
                ex.response.status(),
                ex.response.reason(),
                ex.getMessage(),
                ex.getKeycloakBody(),
                "Keycloak");

        ErrorResponse response = ErrorResponse.builder()
                .status(ex.response.status())
                .errorCode(normalizeErrorCode(ex.response.reason()))
                .errorMessage(ex.getMessage() != null ? ex.getMessage() : "Authentication service error")
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // ────────────────────── UNEXPECTED SYSTEM ERRORS ──────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        int status = 500;
        String reason = "Internal Server Error";
        String body = ex.getClass().getSimpleName() + ": " + ex.getMessage();

        logStructuredError(request, status, reason, ex.getMessage(), body, "System");

        ErrorResponse response = ErrorResponse.builder()
                .status(status)
                .errorCode("SYSTEM_ERROR")
                .errorMessage("Internal server error occurred.")
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // ────────────────────── REUSABLE LOGGING METHOD ──────────────────────
    private void logStructuredError(HttpServletRequest request,
                                    int status,
                                    String reason,
                                    String message,
                                    String body,
                                    String source) {

        log.error("""
                APPLICATION ERROR
                Time        : 2025-11-15 10:48 AM +04
                Source      : {}
                Path        : {}
                Method      : {}
                Status      : {} {}
                Error Code  : {}
                Message     : {}
                Details     : {}
                Trace ID    : {}
                """,
                source,
                request.getRequestURI(),
                request.getMethod(),
                status, reason,
                normalizeErrorCode(reason),
                message != null ? message : "null",
                body != null && body.length() > 500 ? body.substring(0, 500) + "..." : body,
                "trace-id" //                MDC.get("traceId") != null ? MDC.get("traceId") : "none"
        );
    }


    private String normalizeErrorCode(String reason) {
        if (reason == null || reason.isBlank()) return "UNKNOWN_ERROR";
        return reason.toUpperCase().replace(" ", "_").replace("-", "_");
    }
}
