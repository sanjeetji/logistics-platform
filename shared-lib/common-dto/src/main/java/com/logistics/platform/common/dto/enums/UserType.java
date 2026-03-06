package com.logistics.platform.common.dto.enums;

/**
 * Unified User Types across the platform.
 * The behavior of these roles is contextually determined by the Tenant's
 * BusinessModel.
 */
public enum UserType {
    /** Platform Owner with global cross-tenant visibility. */
    SUPER_ADMIN,

    /** Administrator for a specific Logistics Company (Tenant). */
    ADMIN,

    /** Generic user acting for the tenant (B2B Employee). */
    USER,

    /** B2C Customer. */
    CUSTOMER,

    /** Fulfillment agent (B2B Trucker or B2C Rider). */
    DRIVER,

    /** Specific role for managing orders and assignments. */
    DISPATCHER,

    /** Operational role for warehouse/hub management. */
    WAREHOUSE_STAFF,

    /** Role for support and customer service tasks. */
    SUPPORT_AGENT,

    /** Internal Service User for automated background processes. */
    SYSTEM
}
