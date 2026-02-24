package com.logistics.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request body for bulk-toggling multiple features for a tenant at once.
 * Used by the SUPER_ADMIN control panel "Save" button.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkFeatureUpdateRequest {

    private List<FeatureToggleItem> updates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureToggleItem {
        private String featureKey;
        private boolean enabled;
    }
}
