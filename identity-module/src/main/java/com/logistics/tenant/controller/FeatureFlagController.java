package com.logistics.tenant.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.tenant.dto.*;
import com.logistics.tenant.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feature Control Panel — REST API
 *
 * Admin endpoints : /api/v1/admin/features/** (SUPER_ADMIN only)
 * Tenant endpoints : /api/v1/features/** (any authenticated user)
 */
@RestController
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    // ═════════════════════════════════════════════════════════════════════════
    // SUPER_ADMIN — Master Feature List
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/features
     * Returns the complete master list of all platform feature flags.
     */
    @GetMapping("/api/v1/admin/features")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeatureFlagDto>>> getAllFeatures() {
        return ResponseEntity.ok(ApiResponse.success(featureFlagService.getAllFeatures()));
    }

    /**
     * POST /api/v1/admin/features
     * Adds a new feature to the master list.
     */
    @PostMapping("/api/v1/admin/features")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeatureFlagDto>> createFeature(
            @RequestBody FeatureFlagCreateRequest request) {
        FeatureFlagDto created = featureFlagService.createFeature(request);
        return ResponseEntity.ok(ApiResponse.success(created, "Feature created successfully"));
    }

    /**
     * PUT /api/v1/admin/features/{featureKey}/global
     * Updates the global enabled/disabled default for a feature.
     * Body: { "globallyEnabled": true }
     */
    @PutMapping("/api/v1/admin/features/{featureKey}/global")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeatureFlagDto>> updateGlobalFlag(
            @PathVariable String featureKey,
            @RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.get("globallyEnabled");
        if (enabled == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Request body must contain 'globallyEnabled' field"));
        }
        FeatureFlagDto updated = featureFlagService.updateGlobalFlag(featureKey, enabled);
        return ResponseEntity.ok(ApiResponse.success(updated, "Global flag updated"));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SUPER_ADMIN — Per-Tenant Feature Control (The Control Panel)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/features/tenant/{tenantId}
     * Returns ALL features with their ON/OFF state for a specific tenant.
     * This is the primary endpoint for the control panel dashboard.
     */
    @GetMapping("/api/v1/admin/features/tenant/{tenantId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TenantFeatureStatusDto>> getTenantFeatures(
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(ApiResponse.success(
                featureFlagService.getTenantFeatureStatus(tenantId)));
    }

    /**
     * POST /api/v1/admin/features/{featureKey}/enable/{tenantId}
     * Enables a single feature for a specific tenant.
     */
    @PostMapping("/api/v1/admin/features/{featureKey}/enable/{tenantId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enableFeature(
            @PathVariable String featureKey,
            @PathVariable Long tenantId) {
        featureFlagService.enableFeatureForTenant(featureKey, String.valueOf(tenantId));
        return ResponseEntity.ok(ApiResponse.success(null,
                "Feature " + featureKey + " enabled for tenant " + tenantId));
    }

    /**
     * POST /api/v1/admin/features/{featureKey}/disable/{tenantId}
     * Disables a single feature for a specific tenant.
     */
    @PostMapping("/api/v1/admin/features/{featureKey}/disable/{tenantId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disableFeature(
            @PathVariable String featureKey,
            @PathVariable Long tenantId) {
        featureFlagService.disableFeatureForTenant(featureKey, String.valueOf(tenantId));
        return ResponseEntity.ok(ApiResponse.success(null,
                "Feature " + featureKey + " disabled for tenant " + tenantId));
    }

    /**
     * PUT /api/v1/admin/features/tenant/{tenantId}/bulk
     * Bulk-toggle multiple features for a tenant in one call.
     * Used by the SUPER_ADMIN control panel "Save All" button.
     *
     * Body:
     * {
     * "updates": [
     * { "featureKey": "ROUTE_OPTIMIZATION", "enabled": true },
     * { "featureKey": "DYNAMIC_PRICING", "enabled": false }
     * ]
     * }
     */
    @PutMapping("/api/v1/admin/features/tenant/{tenantId}/bulk")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> bulkUpdateTenantFeatures(
            @PathVariable Long tenantId,
            @RequestBody BulkFeatureUpdateRequest request) {
        featureFlagService.bulkUpdateForTenant(tenantId, request);
        return ResponseEntity.ok(ApiResponse.success(null,
                "Feature flags updated for tenant " + tenantId));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TENANT SELF-SERVICE — any authenticated user
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/features/my-features
     * Returns the list of enabled feature keys for the calling user's tenant.
     * Called by the frontend on login to know which UI sections to show/hide.
     *
     * Requires the JWT to include tenantId (organizationId) claim.
     */
    @GetMapping("/api/v1/features/my-features")
    public ResponseEntity<ApiResponse<MyFeaturesResponse>> getMyFeatures(
            @RequestHeader(value = "X-Tenant-Id", required = false) Long headerTenantId,
            @RequestParam(value = "tenantId", required = false) Long paramTenantId) {

        Long tenantId = headerTenantId != null ? headerTenantId : paramTenantId;

        if (tenantId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse
                            .error("Tenant ID must be provided via X-Tenant-Id header or tenantId query param"));
        }

        return ResponseEntity.ok(ApiResponse.success(
                featureFlagService.getMyEnabledFeatures(tenantId)));
    }
}
