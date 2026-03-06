package com.logistics.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for tenant's own feature list.
 * Called by any logged-in user to know which features are ON for their tenant.
 * Frontend uses this on login to show/hide menu items, pages, and buttons.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyFeaturesResponse {

    private Long tenantId;

    /** List of feature keys that are currently enabled for this tenant */
    private List<String> enabledFeatures;
}
