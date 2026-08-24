package com.zs.spring_security_rbac.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String email,
        String role,
        List<String> permissions
) {
    public static AuthResponse of(String token, String email, String role, List<String> permissions) {
        return new AuthResponse(token, "Bearer", email, role, permissions);
    }
}
