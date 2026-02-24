package com.logistics.tenant.service;

import com.logistics.tenant.exception.FeatureNotEnabledException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * FeatureGuardService — reusable guard utility for business logic.
 *
 * Usage inside any service class:
 *
 * @Autowired FeatureGuardService featureGuard;
 *
 *            // At the top of any method that requires a feature:
 *            featureGuard.require("ROUTE_OPTIMIZATION", tenantId);
 *
 *            If the feature is disabled for that tenant → throws
 *            FeatureNotEnabledException (HTTP 403).
 *            If the feature is enabled → continues silently.
 */
@Service
@RequiredArgsConstructor
public class FeatureGuardService {

    private final FeatureFlagService featureFlagService;

    /**
     * Asserts that a feature is enabled for the given tenant.
     * Throws FeatureNotEnabledException (HTTP 403) if not.
     *
     * @param featureKey the feature key, e.g. "ROUTE_OPTIMIZATION"
     * @param tenantId   the tenant/organization ID
     */
    public void require(String featureKey, Long tenantId) {
        boolean enabled = featureFlagService.isFeatureEnabled(
                featureKey,
                String.valueOf(tenantId));
        if (!enabled) {
            throw new FeatureNotEnabledException(featureKey, tenantId);
        }
    }

    /**
     * Returns true if a feature is enabled for the given tenant, false otherwise.
     * Use this when you want to conditionally change behavior rather than throw.
     *
     * @param featureKey the feature key
     * @param tenantId   the tenant/organization ID
     */
    public boolean isEnabled(String featureKey, Long tenantId) {
        return featureFlagService.isFeatureEnabled(featureKey, String.valueOf(tenantId));
    }
}
