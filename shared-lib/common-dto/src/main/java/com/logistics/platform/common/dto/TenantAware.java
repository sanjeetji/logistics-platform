package com.logistics.platform.common.dto;

/**
 * Interface for entities that are tenant-aware.
 * This ensures they have a tenantId field which can be automatically populated.
 */
public interface TenantAware {

    String getTenantId();

    void setTenantId(String tenantId);
}
