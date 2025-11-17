package com.example.auth.model.request;

import java.util.List;

public record CreateResourceRequest(
        String resourceName,
        String uriPattern,           // e.g. "/api/orders/**"
        String policyName,           // e.g. "Only Admin Can Write Orders"
        List<String> allowedMethods  // e.g. List.of("GET", "POST")
) {}

