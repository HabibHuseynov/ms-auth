package com.example.auth.model.request;

import java.util.List;

public record CreateRolePolicyRequest(String policyName, String description, List<String> roles) {}
