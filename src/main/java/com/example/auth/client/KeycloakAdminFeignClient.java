package com.example.auth.client;

import com.example.auth.model.request.CreateRoleRequest;
import org.keycloak.representations.idm.ClientRepresentation;
import com.example.auth.model.request.PasswordCredential;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "keycloak-admin",
        url = "${keycloak.auth-server-url}",
        configuration = KeycloakAdminFeignConfig.class)
public interface KeycloakAdminFeignClient {

    @PostMapping(value="/admin/realms/{realm}/roles")
    ResponseEntity<Void> createRealmRole(@PathVariable String realm,
                                         @RequestBody CreateRoleRequest role);

    @GetMapping("/admin/realms/{realm}/roles")
    List<RoleRepresentation> listRealmRoles(@PathVariable String realm);

    @GetMapping("/admin/realms/{realm}/roles/{roleName}")
    RoleRepresentation getRealmRole(@PathVariable String realm,
                                    @PathVariable String roleName);

    @PostMapping("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    ResponseEntity<Void> addRealmRolesToUser(@PathVariable String realm,
                                             @PathVariable String userId,
                                             @RequestBody List<RoleRepresentation> roles);

    @DeleteMapping("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    ResponseEntity<Void> removeRealmRolesFromUser(@PathVariable String realm,
                                                  @PathVariable String userId,
                                                  @RequestBody List<RoleRepresentation> roles);

    @GetMapping("/admin/realms/{realm}/users/{userId}/role-mappings/realm/composite")
    List<RoleRepresentation> getEffectiveRealmRoles(@PathVariable String realm,
                                                    @PathVariable String userId);

    @GetMapping("/admin/realms/{realm}/users")
    List<UserRepresentation> searchUsers(@PathVariable String realm,
                                         @RequestParam String search);

    @GetMapping("/admin/realms/{realm}/clients")
    List<ClientRepresentation> findClients(@PathVariable String realm,
                                           @RequestParam("clientId") String clientId);

    // User management (admin)
    @PostMapping(
            value = "/admin/realms/{realm}/users",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> createUser(
            @PathVariable("realm") String realm,
            @RequestBody UserRepresentation user
    );

    @PutMapping(
            value = "/admin/realms/{realm}/users/{userId}/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> resetPassword(
            @PathVariable("realm") String realm,
            @PathVariable("userId") String userId,
            @RequestBody PasswordCredential password
    );
}
