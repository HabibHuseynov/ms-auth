package com.example.auth.model.request;

import java.util.List;
import java.util.Set;

public record AssignResourceToRoleRequest(
        List<String> resourceId,
        Set<String> scopes,
        String roleName
) {}
