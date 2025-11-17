package com.example.auth.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoResponse(
        @JsonProperty("sub") String sub,
        @JsonProperty("name") String name,
        @JsonProperty("preferred_username") String preferredUsername,
        @JsonProperty("given_name") String givenName,
        @JsonProperty("family_name") String familyName,
        @JsonProperty("email") String email,
        @JsonProperty("email_verified") Boolean emailVerified,
        @JsonProperty("roles") List<String> roles
) {}
