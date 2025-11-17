package com.example.auth.model.request;

import java.util.List;
import java.util.Map;

public record PermissionCheckRequest(String resource, String scope) {}

