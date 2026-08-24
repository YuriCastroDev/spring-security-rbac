package com.zs.spring_security_rbac.dto;

public record RegisterRequest(String name, String email, String password, String role) {}
