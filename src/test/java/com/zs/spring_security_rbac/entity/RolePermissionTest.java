package com.zs.spring_security_rbac.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RolePermissionTest {

    @Test
    void viewerShouldOnlyHaveReadPermissions() {
        List<SimpleGrantedAuthority> authorities = Role.ROLE_VIEWER.getAuthorities();
        List<String> permissions = authorities.stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        assertThat(permissions).contains("user:read", "product:read", "order:read");
        assertThat(permissions).doesNotContain("user:write", "user:delete", "product:write");
    }

    @Test
    void userShouldHaveReadAndWriteButNotDelete() {
        List<String> permissions = Role.ROLE_USER.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        assertThat(permissions).contains("product:read", "product:write", "order:write");
        assertThat(permissions).doesNotContain("product:delete", "user:delete", "order:delete");
    }

    @Test
    void moderatorShouldHaveReportReadButNotExport() {
        List<String> permissions = Role.ROLE_MODERATOR.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        assertThat(permissions).contains("report:read", "user:write");
        assertThat(permissions).doesNotContain("report:export", "user:delete");
    }

    @Test
    void adminShouldHaveAllPermissions() {
        List<String> permissions = Role.ROLE_ADMIN.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        assertThat(permissions).contains(
                "user:read", "user:write", "user:delete",
                "product:read", "product:write", "product:delete",
                "order:read", "order:write", "order:delete",
                "report:read", "report:export"
        );
    }

    @Test
    void authoritiesShouldIncludeRoleItself() {
        List<String> adminAuthorities = Role.ROLE_ADMIN.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        assertThat(adminAuthorities).contains("ROLE_ADMIN");

        List<String> viewerAuthorities = Role.ROLE_VIEWER.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();

        assertThat(viewerAuthorities).contains("ROLE_VIEWER");
    }

    @Test
    void adminShouldHaveMorePermissionsThanModerator() {
        long adminCount = Role.ROLE_ADMIN.getPermissions().size();
        long modCount = Role.ROLE_MODERATOR.getPermissions().size();
        assertThat(adminCount).isGreaterThan(modCount);
    }
}
