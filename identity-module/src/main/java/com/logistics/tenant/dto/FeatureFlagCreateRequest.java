package com.logistics.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a single feature flag.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagCreateRequest {

    private String featureKey;
    private String featureName;
    private String description;
    private String category;
    private Boolean globallyEnabled;
}
