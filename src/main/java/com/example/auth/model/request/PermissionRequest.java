package com.example.auth.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PermissionRequest(
        @JsonProperty("grant_type")
        String grantType,
        String audience,

        @JsonProperty("permission")
        String resourcePermission,

        @JsonProperty("http_method")
        String httpMethod,
        String uri
) {
    public static PermissionRequest ofPath(String audience, String uri, String httpMethod) {
        return new PermissionRequest(
                "urn:ietf:params:oauth:grant-type:uma-ticket",
                audience,
                null,
                httpMethod,
                uri
        );
    }

    public static PermissionRequest ofResource(String audience, String resourceName, String scope) {
        return new PermissionRequest(
                "urn:ietf:params:oauth:grant-type:uma-ticket",
                audience,
                resourceName + "#" + scope,
                null,
                null
        );
    }
}
