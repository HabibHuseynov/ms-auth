package com.example.auth.controller;

import com.example.auth.model.request.PermissionCheckRequest;
import com.example.auth.service.KeycloakAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AccessController {

    private final KeycloakAdminService permissionService;

    @PostMapping("/check-access")
    public ResponseEntity<Void> checkAccess(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PermissionCheckRequest request) {
        permissionService.checkPermissionAndGetUserInfo(request);
        return ResponseEntity.ok().build();
    }
}
