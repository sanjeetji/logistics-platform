package com.logistics.platform.utils.tenant;

import com.logistics.platform.common.dto.TenantAware;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * JPA Entity Listener to automatically populate tenant ID from security
 * context.
 */
public class TenantListener {

    @PrePersist
    @PreUpdate
    public void setTenantId(Object entity) {
        if (entity instanceof TenantAware) {
            TenantAware tenantAware = (TenantAware) entity;
            if (tenantAware.getTenantId() == null) {
                String tenantId = getTenantIdFromContext();
                if (tenantId != null) {
                    tenantAware.setTenantId(tenantId);
                }
            }
        }
    }

    private String getTenantIdFromContext() {
        return TenantContextUtils.getTenantId();
    }
}
