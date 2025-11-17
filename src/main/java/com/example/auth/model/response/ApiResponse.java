package com.example.auth.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public static ApiResponse success(String message) {
        return ApiResponse.builder().success(true).message(message).build();
    }
    public static ApiResponse success(String message, Object data) {
        return ApiResponse.builder().success(true).message(message).data(data).build();
    }
}
