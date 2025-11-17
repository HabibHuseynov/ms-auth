package com.example.auth.model.request;


import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateRoleRequest(
        @JsonProperty("name") String name, @JsonProperty("description") String description) {}


