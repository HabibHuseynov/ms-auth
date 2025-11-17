package com.example.auth.model.response;

import java.util.List;
import java.util.Map;

public record PermissionCheckResponse(
        boolean allowed,
        Map<String, Object> userInfo,
        List<String> permissions // optional: granted permissions
) {}
