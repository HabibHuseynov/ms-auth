package com.example.auth.service;

import com.example.auth.client.KeycloakAdminFeignClient;
import com.example.auth.client.KeycloakAuthClient;
import com.example.auth.client.KeycloakAuthzFeignClient;
import com.example.auth.model.request.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final KeycloakAdminFeignClient adminClient;
    private final KeycloakAuthzFeignClient authzClient;
    private final KeycloakAuthClient keycloakAuthClient;

    @Value("${keycloak.realm}") private String REALM;
    @Value("${keycloak.resource}") private String CLIENT_ID;


    public void createRole(CreateRoleRequest req) {
        adminClient.createRealmRole(REALM, req);
        log.info("Role created: {} by admin", req.name());
    }

    public List<RoleRepresentation> getAllRoles() {
        return adminClient.listRealmRoles(REALM);
    }

    public RoleRepresentation getRoleByName(String roleName) {
        return adminClient.getRealmRole(REALM, roleName);
    }

    public void assignRoleToUser(String userId, AssignRoleRequest req) {
        RoleRepresentation role = adminClient.getRealmRole(REALM, req.roleName());
        adminClient.addRealmRolesToUser(REALM, userId, List.of(role));
        log.info("Role {} assigned to user {}", req.roleName(), userId);
    }

    public void removeRoleFromUser(String userId, String roleName) {
        RoleRepresentation role = adminClient.getRealmRole(REALM, roleName);
        adminClient.removeRealmRolesFromUser(REALM, userId, List.of(role));
        log.info("Role {} removed from user {}", roleName, userId);
    }

    public List<RoleRepresentation> getUserRoles(String userId) {
        return adminClient.getEffectiveRealmRoles(REALM, userId);
    }

    public List<UserRepresentation> searchUsers(String search) {
        return adminClient.searchUsers(REALM, search);
    }

    public void createResource(CreateResourceRequest req) {
        ResourceRepresentation resource = new ResourceRepresentation();
        resource.setName(req.uriPattern());
        resource.setUri(req.uriPattern());
        resource.setType("urn:" + CLIENT_ID + ":resources");
        resource.setScopes(req.allowedMethods().stream()
                .map(m -> {
                    ScopeRepresentation s = new ScopeRepresentation();
                    s.setName(m);
                    return s;
                })
                .collect(Collectors.toSet()));

        authzClient.createResource(REALM, getClientUuid(), resource);
    }

    public void createScope(String scopeName) {
        ScopeRepresentation scope = new ScopeRepresentation();
        scope.setName(scopeName);
        authzClient.createScope(REALM, getClientUuid(), scope);
        log.info("Scope created: {}", scopeName);
    }

    public void createRolePolicy(CreateRolePolicyRequest req) {
        PolicyRepresentation policy = new PolicyRepresentation();
        policy.setName(req.policyName());
        policy.setDescription(req.description());
        policy.setType("role");

        Map<String, String> config = new HashMap<>();
        String rolesJson = req.roles().stream()
                .map(r -> "{\"id\":\"" + r + "\",\"required\":true}")
                .collect(Collectors.joining(","));
        config.put("roles", "[%s]".formatted(rolesJson));

        policy.setConfig(config);
        authzClient.createPolicy(REALM, getClientUuid(), policy);
    }

    public void createPermission(CreatePermissionRequest req) {
        ResourcePermissionRepresentation perm = new ResourcePermissionRepresentation();
        perm.setName(req.name());
        perm.addResource(req.resourceId());
        perm.setScopes(req.scopes());
        req.policyIds().forEach(perm::addPolicy);

        authzClient.createResourcePermission(REALM, getClientUuid(), perm);
        log.info("Permission created: {} → scopes: {}", req.name(), req.scopes());
    }


    private String getClientUuid() {
        return adminClient.findClients(REALM, CLIENT_ID)
                .stream()
                .findFirst()
                .map(ClientRepresentation::getId)
                .orElseThrow(() -> new IllegalStateException("Client " + CLIENT_ID + " not found"));
    }

    public List<ResourceRepresentation> findResourceIdByUri(String uri) {
        return authzClient.listResources(REALM, getClientUuid());
    }

    public String findPolicyIdByName(String name) {
        return authzClient.listPolicies(REALM, getClientUuid()).stream()
                .filter(p -> name.equals(p.getName()))
                .map(PolicyRepresentation::getId)
                .findFirst()
                .orElse(null);
    }

    public void assignResourceToRole(AssignResourceToRoleRequest req) {
        String clientUuid = getClientUuid();

        String policyId = findOrCreateRolePolicy(req.roleName());
        ResourcePermissionRepresentation perm = new ResourcePermissionRepresentation();
        perm.setName("Permission: " + req.resourceId() + " → " + req.roleName());
        req.resourceId().forEach(perm::addResource);
        perm.setScopes(req.scopes());
        perm.addPolicy(policyId);

        authzClient.createResourcePermission(REALM, clientUuid, perm);
        log.info("Resource '{}' with scopes {} assigned to role '{}'", req.resourceId(), req.scopes(), req.roleName());
    }

    public void checkPermissionAndGetUserInfo(PermissionCheckRequest request) {

        Map<String, Object> formParams = new HashMap<>();
        formParams.put("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
        formParams.put("client_id", CLIENT_ID);
        formParams.put("permission", request.resource() + "#" + request.scope());
        formParams.put("audience", CLIENT_ID);
        keycloakAuthClient.getRpt(formParams);
    }

    private String findOrCreateRolePolicy(String roleName) {
        String clientUuid = getClientUuid();

        Optional<PolicyRepresentation> existing = authzClient.listPolicies(REALM, clientUuid).stream()
                .filter(p -> "role".equals(p.getType()))
                .filter(p -> p.getConfig() != null && p.getConfig().get("roles") != null)
                .filter(p -> p.getConfig().get("roles").contains("\"id\":\"" + roleName + "\""))
                .findFirst();

        if (existing.isPresent()) {
            return existing.get().getId();
        }

        PolicyRepresentation policy = new PolicyRepresentation();
        policy.setName("Auto: Role Policy for " + roleName);
        policy.setType("role");
        Map<String, String> config = new HashMap<>();
        config.put("roles", "[{\"id\":\"" + roleName + "\",\"required\":\"true\"}]");
        policy.setConfig(config);

        authzClient.createPolicy(REALM, clientUuid, policy);
        log.info("Created role policy for: {}", roleName);

        return authzClient.listPolicies(REALM, clientUuid).stream()
                .filter(p -> policy.getName().equals(p.getName()))
                .map(PolicyRepresentation::getId)
                .findFirst()
                .orElseThrow();
    }
}
