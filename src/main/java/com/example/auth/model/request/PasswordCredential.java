package com.example.auth.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PasswordCredential(
        @JsonProperty("type") String type,
        @JsonProperty("value") String value,
        @JsonProperty("temporary") Boolean temporary
) {}
