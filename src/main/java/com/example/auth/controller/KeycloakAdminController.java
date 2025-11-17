package com.example.auth.controller;

import com.example.auth.model.request.*;
import com.example.auth.model.response.ApiResponse;
import com.example.auth.service.KeycloakAdminService;
import com.example.auth.service.KeycloakAuthService;
import jakarta.ws.rs.container.ResourceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/keycloak")
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminController {

    private final KeycloakAdminService adminService;
    private final KeycloakAuthService keycloakAuthService;

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse> createRole(@RequestBody CreateRoleRequest req) {
        adminService.createRole(req);
        return ResponseEntity.ok(ApiResponse.success("Role created successfully"));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getAllRoles() {
        var roles = adminService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles fetched", roles));
    }

    @GetMapping("/roles/{roleName}")
    public ResponseEntity<ApiResponse> getRole(@PathVariable String roleName) {
        var role = adminService.getRoleByName(roleName);
        return ResponseEntity.ok(ApiResponse.success("Role found", role));
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse> assignRole(
            @PathVariable String userId,
            @RequestBody AssignRoleRequest req) {
        adminService.assignRoleToUser(userId, req);
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully"));
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<ApiResponse> removeRole(
            @PathVariable String userId,
            @PathVariable String roleName) {
        adminService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok(ApiResponse.success("Role removed"));
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse> getUserRoles(@PathVariable String userId) {
        var roles = adminService.getUserRoles(userId);
        return ResponseEntity.ok(ApiResponse.success("User roles", roles));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse> searchUsers(@RequestParam String q) {
        var users = adminService.searchUsers(q);
        return ResponseEntity.ok(ApiResponse.success("Users found", users));
    }

    @PostMapping("/authorization/resources")
    public ResponseEntity<ApiResponse> createResource(
            @RequestBody CreateResourceRequest req) {
        adminService.createResource(req);
        return ResponseEntity.ok(ApiResponse.success("Resource created successfully"));
    }

    @PostMapping("/authorization/scopes")
    public ResponseEntity<ApiResponse> createScope(
            @RequestBody CreateScopeRequest req) {
        adminService.createScope(req.scopeName());
        return ResponseEntity.ok(ApiResponse.success("Scope created: " + req.scopeName()));
    }

    @PostMapping("/authorization/policies/role")
    public ResponseEntity<ApiResponse> createRolePolicy(
            @RequestBody CreateRolePolicyRequest req) {
        adminService.createRolePolicy(req);
        return ResponseEntity.ok(ApiResponse.success("Role-based policy created: " + req.policyName()));
    }

    @PostMapping("/authorization/permissions")
    public ResponseEntity<ApiResponse> createPermission(
            @RequestBody CreatePermissionRequest req) {
        adminService.createPermission(req);
        return ResponseEntity.ok(ApiResponse.success("Permission created: " + req.name()));
    }

    @GetMapping("/authorization/resource/{name}")
    public ResponseEntity<List<ResourceRepresentation>> getResource(@PathVariable String name) {
        return ResponseEntity.ok(adminService.findResourceIdByUri(name));
    }

    @PostMapping("/authorization/resource/assign/role")
    public ResponseEntity<Void> getResource(@RequestBody AssignResourceToRoleRequest req) {
        adminService.assignResourceToRole(req);
        return ResponseEntity.ok().build();
    }
}
