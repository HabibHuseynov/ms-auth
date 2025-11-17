package com.example.auth.client;

import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "keycloak-authz",
        url = "${keycloak.auth-server-url}",
        configuration = KeycloakAdminFeignConfig.class)
public interface KeycloakAuthzFeignClient {

    @PostMapping(value = "/admin/realms/{realm}/clients/{clientId}/authz/resource-server/resource")
    ResponseEntity<Void> createResource(@PathVariable String realm,
                                        @PathVariable String clientId,
                                        @RequestBody ResourceRepresentation resource);

    @GetMapping("/admin/realms/{realm}/clients/{clientId}/authz/resource-server/resource")
    List<ResourceRepresentation> listResources(@PathVariable String realm,
                                               @PathVariable String clientId);

    @PostMapping("/admin/realms/{realm}/clients/{clientId}/authz/scope")
    ResponseEntity<Void> createScope(@PathVariable String realm,
                                     @PathVariable String clientId,
                                     @RequestBody ScopeRepresentation scope);

    @PostMapping("/admin/realms/{realm}/clients/{clientId}/authz/resource-server/policy")
    ResponseEntity<Void> createPolicy(@PathVariable String realm,
                                      @PathVariable String clientId,
                                      @RequestBody PolicyRepresentation policy);

    @GetMapping("/admin/realms/{realm}/clients/{clientId}/authz/resource-server/policy")
    List<PolicyRepresentation> listPolicies(@PathVariable String realm,
                                            @PathVariable String clientId);

    @PostMapping("/admin/realms/{realm}/clients/{clientId}/authz/resource-server/permission/resource")
    ResponseEntity<Void> createResourcePermission(@PathVariable String realm,
                                                  @PathVariable String clientId,
                                                  @RequestBody ResourcePermissionRepresentation permission);



}
