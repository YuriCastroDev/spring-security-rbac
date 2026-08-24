package com.zs.spring_security_rbac.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    // User management
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),

    // Product management
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    PRODUCT_DELETE("product:delete"),

    // Order management
    ORDER_READ("order:read"),
    ORDER_WRITE("order:write"),
    ORDER_DELETE("order:delete"),

    // Report management
    REPORT_READ("report:read"),
    REPORT_EXPORT("report:export");

    private final String permission;
}
