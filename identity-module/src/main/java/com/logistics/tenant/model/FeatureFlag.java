package com.logistics.tenant.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "feature_flags")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlag extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String featureKey; // e.g., "ROUTE_OPTIMIZATION", "REAL_TIME_TRACKING"

    @Column(nullable = false)
    private String featureName;

    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Boolean globallyEnabled = false;

    // Tenant-specific overrides: { "tenant-123": true, "tenant-456": false }
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    @Builder.Default
    private Map<String, Boolean> tenantOverrides = new HashMap<>();

    public boolean isEnabledForTenant(String tenantId) {
        // Check tenant-specific override first
        if (tenantOverrides != null && tenantOverrides.containsKey(tenantId)) {
            return tenantOverrides.get(tenantId);
        }
        // Fall back to global setting
        return globallyEnabled;
    }

    public void enableForTenant(String tenantId) {
        if (tenantOverrides == null) {
            tenantOverrides = new HashMap<>();
        }
        tenantOverrides.put(tenantId, true);
    }

    public void disableForTenant(String tenantId) {
        if (tenantOverrides == null) {
            tenantOverrides = new HashMap<>();
        }
        tenantOverrides.put(tenantId, false);
    }
}
