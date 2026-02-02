package com.logistics.platform.common.dto.enums;

public enum UserType {
    // Platform Owner (Super Admin) - Manages all tenants and global settings
    SUPER_ADMIN,

    // B2B Roles (Bringg-style)
    TENANT_ADMIN, // Admin for a specific Logistics Company (Tenant)
    TENANT_USER, // Generic Staff/User for a Tenant
    DISPATCHER,
    DRIVER,

    // B2C Roles (Porter-style)
    CUSTOMER,
    RIDER
}
