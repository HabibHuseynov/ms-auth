package com.example.auth.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;


public record RptRequest(
        @JsonProperty("grant_type") String grantType,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("permission") String permission
) {
    public static RptRequest uma(String clientId, String resource, String scope) {
        return new RptRequest(
                "urn:ietf:params:oauth:grant-type:uma-ticket",
                clientId,
                resource + "#" + scope
        );
    }
}
