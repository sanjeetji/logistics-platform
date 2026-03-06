package com.logistics.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a single feature flag (used in admin master list responses)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagDto {

    private Long id;
    private String featureKey;
    private String featureName;
    private String description;
    private String category;
    private Boolean globallyEnabled;
}
