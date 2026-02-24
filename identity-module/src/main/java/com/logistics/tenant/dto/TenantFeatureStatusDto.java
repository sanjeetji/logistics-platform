package com.logistics.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO showing ON/OFF status of all features for a specific tenant.
 * Used by the SUPER_ADMIN control panel dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantFeatureStatusDto {

    private Long tenantId;
    private String tenantName;
    private String subscriptionTier;

    /**
     * Full list of platform features with enabled/disabled state for this tenant
     */
    private List<TenantFeatureItem> features;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantFeatureItem {
        private String featureKey;
        private String featureName;
        private String category;
        private String description;
        private boolean enabled;
    }
}
