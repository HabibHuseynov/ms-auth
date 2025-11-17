package com.example.auth.controller;

import com.example.auth.model.request.LoginRequest;
import com.example.auth.model.request.PermissionCheckRequest;
import com.example.auth.model.request.RefreshRequest;
import com.example.auth.model.request.RegisterRequest;
import com.example.auth.model.response.LoginResponse;
import com.example.auth.model.response.UserInfoResponse;
import com.example.auth.model.response.PermissionCheckResponse;
import com.example.auth.service.KeycloakAdminService;
import com.example.auth.service.KeycloakAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakAuthService authService;
    private final KeycloakAdminService permissionService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.registerUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me() {
        return ResponseEntity.ok(authService.getUserInfo());
    }

    @PostMapping("/check")
    public ResponseEntity<PermissionCheckResponse> check(
            @RequestHeader("Authorization") String auth,
            @RequestBody PermissionCheckRequest req) {

        permissionService.checkPermissionAndGetUserInfo(req);
        return ResponseEntity.ok(null);
    }
}
