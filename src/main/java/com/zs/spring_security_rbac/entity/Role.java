package com.zs.spring_security_rbac.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_VIEWER(Set.of(
            Permission.USER_READ,
            Permission.PRODUCT_READ,
            Permission.ORDER_READ
    )),

    ROLE_USER(Set.of(
            Permission.USER_READ,
            Permission.PRODUCT_READ,
            Permission.PRODUCT_WRITE,
            Permission.ORDER_READ,
            Permission.ORDER_WRITE
    )),

    ROLE_MODERATOR(Set.of(
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.PRODUCT_READ,
            Permission.PRODUCT_WRITE,
            Permission.ORDER_READ,
            Permission.ORDER_WRITE,
            Permission.REPORT_READ
    )),

    ROLE_ADMIN(Set.of(
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.USER_DELETE,
            Permission.PRODUCT_READ,
            Permission.PRODUCT_WRITE,
            Permission.PRODUCT_DELETE,
            Permission.ORDER_READ,
            Permission.ORDER_WRITE,
            Permission.ORDER_DELETE,
            Permission.REPORT_READ,
            Permission.REPORT_EXPORT
    ));

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        // Retorna tanto as permissões quanto a role em si
        return Stream.concat(
                permissions.stream()
                        .map(p -> new SimpleGrantedAuthority(p.getPermission())),
                Stream.of(new SimpleGrantedAuthority(this.name()))
        ).collect(Collectors.toList());
    }
}
