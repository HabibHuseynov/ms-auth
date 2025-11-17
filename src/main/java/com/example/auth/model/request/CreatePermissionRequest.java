package com.example.auth.model.request;

import java.util.List;
import java.util.Set;

public record CreatePermissionRequest(String name, String resourceId, Set<String> scopes, List<String> policyIds) {}
