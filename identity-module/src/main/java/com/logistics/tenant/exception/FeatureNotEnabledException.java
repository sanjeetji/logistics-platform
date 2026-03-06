package com.logistics.tenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a SUPER_ADMIN-disabled feature is accessed by a tenant.
 * Returns HTTP 403 Forbidden.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class FeatureNotEnabledException extends RuntimeException {

    private final String featureKey;
    private final Long tenantId;

    public FeatureNotEnabledException(String featureKey, Long tenantId) {
        super("Feature '" + featureKey + "' is not available for your account. " +
                "If you believe this is an error, please contact support.");
        this.featureKey = featureKey;
        this.tenantId = tenantId;
    }

    public String getFeatureKey() {
        return featureKey;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
