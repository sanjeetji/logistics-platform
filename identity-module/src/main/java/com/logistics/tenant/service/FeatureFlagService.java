package com.logistics.tenant.service;

import com.logistics.tenant.dto.*;
import com.logistics.tenant.model.FeatureFlag;
import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.repository.FeatureFlagRepository;
import com.logistics.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;
    private final TenantRepository tenantRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // CHECK (used by business logic guards inside other services)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns true if the feature is enabled for the given tenant.
     * Result is cached per featureKey + tenantId combination.
     */
    @Cacheable(value = "featureFlags", key = "#featureKey + ':' + #tenantId")
    public boolean isFeatureEnabled(String featureKey, String tenantId) {
        return featureFlagRepository.findByFeatureKey(featureKey)
                .map(flag -> flag.isEnabledForTenant(tenantId))
                .orElse(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN — Master Feature List
    // ─────────────────────────────────────────────────────────────────────────

    /** Returns all platform feature flags, ordered by category then name */
    public List<FeatureFlagDto> getAllFeatures() {
        return featureFlagRepository.findAllByOrderByCategoryAscFeatureNameAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Create a new feature in the master list (called by SUPER_ADMIN) */
    @Transactional
    public FeatureFlagDto createFeature(FeatureFlagCreateRequest req) {
        if (featureFlagRepository.existsByFeatureKey(req.getFeatureKey())) {
            throw new IllegalArgumentException("Feature key already exists: " + req.getFeatureKey());
        }
        FeatureFlag flag = FeatureFlag.builder()
                .featureKey(req.getFeatureKey())
                .featureName(req.getFeatureName())
                .description(req.getDescription())
                .category(req.getCategory())
                .globallyEnabled(req.getGloballyEnabled() != null && req.getGloballyEnabled())
                .build();
        return toDto(featureFlagRepository.save(flag));
    }

    /** Update the global enabled/disabled default for a feature */
    @Transactional
    @CacheEvict(value = "featureFlags", allEntries = true)
    public FeatureFlagDto updateGlobalFlag(String featureKey, boolean globallyEnabled) {
        FeatureFlag flag = getByKey(featureKey);
        flag.setGloballyEnabled(globallyEnabled);
        log.info("Updated global flag {} to {}", featureKey, globallyEnabled);
        return toDto(featureFlagRepository.save(flag));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN — Per-Tenant Feature Control
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns all platform features with their ON/OFF state for a specific tenant.
     * This powers the SUPER_ADMIN control panel dashboard.
     */
    public TenantFeatureStatusDto getTenantFeatureStatus(Long tenantId) {
        Tenant tenant = tenantRepository.findById(Objects.requireNonNull(tenantId, "tenantId must not be null"))
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));

        String tenantIdStr = String.valueOf(tenantId);

        List<TenantFeatureStatusDto.TenantFeatureItem> featureItems = featureFlagRepository
                .findAllByOrderByCategoryAscFeatureNameAsc()
                .stream()
                .map(flag -> TenantFeatureStatusDto.TenantFeatureItem.builder()
                        .featureKey(flag.getFeatureKey())
                        .featureName(flag.getFeatureName())
                        .category(flag.getCategory())
                        .description(flag.getDescription())
                        .enabled(flag.isEnabledForTenant(tenantIdStr))
                        .build())
                .collect(Collectors.toList());

        return TenantFeatureStatusDto.builder()
                .tenantId(tenantId)
                .tenantName(tenant.getName())
                .subscriptionTier(tenant.getSubscriptionTier() != null
                        ? tenant.getSubscriptionTier().name()
                        : "FREE")
                .features(featureItems)
                .build();
    }

    /** Enable a single feature for a specific tenant */
    @Transactional
    @CacheEvict(value = "featureFlags", key = "#featureKey + ':' + #tenantId")
    public void enableFeatureForTenant(String featureKey, String tenantId) {
        FeatureFlag flag = getByKey(featureKey);
        flag.enableForTenant(tenantId);
        featureFlagRepository.save(flag);
        log.info("Enabled feature {} for tenant {}", featureKey, tenantId);
    }

    /** Disable a single feature for a specific tenant */
    @Transactional
    @CacheEvict(value = "featureFlags", key = "#featureKey + ':' + #tenantId")
    public void disableFeatureForTenant(String featureKey, String tenantId) {
        FeatureFlag flag = getByKey(featureKey);
        flag.disableForTenant(tenantId);
        featureFlagRepository.save(flag);
        log.info("Disabled feature {} for tenant {}", featureKey, tenantId);
    }

    /**
     * Bulk-toggle multiple features for a tenant in one transaction.
     * This is called by the SUPER_ADMIN control panel "Save" button.
     */
    @Transactional
    @CacheEvict(value = "featureFlags", allEntries = true)
    public void bulkUpdateForTenant(Long tenantId, BulkFeatureUpdateRequest req) {
        String tenantIdStr = String.valueOf(tenantId);

        // Validate tenant exists
        if (!tenantRepository.existsById(Objects.requireNonNull(tenantId, "tenantId must not be null"))) {
            throw new RuntimeException("Tenant not found: " + tenantId);
        }

        List<String> featureKeys = req.getUpdates().stream()
                .map(BulkFeatureUpdateRequest.FeatureToggleItem::getFeatureKey)
                .collect(Collectors.toList());

        List<FeatureFlag> flags = featureFlagRepository.findByFeatureKeyIn(featureKeys);

        for (FeatureFlag flag : flags) {
            req.getUpdates().stream()
                    .filter(update -> update.getFeatureKey().equals(flag.getFeatureKey()))
                    .findFirst()
                    .ifPresent(update -> {
                        if (update.isEnabled()) {
                            flag.enableForTenant(tenantIdStr);
                        } else {
                            flag.disableForTenant(tenantIdStr);
                        }
                    });
        }

        featureFlagRepository.saveAll(Objects.requireNonNull(flags, "flags must not be null"));
        log.info("Bulk updated {} features for tenant {}", flags.size(), tenantId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TENANT SELF-SERVICE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns a list of enabled feature keys for a given tenant.
     * Called by the frontend on login to decide which UI elements to show.
     */
    public MyFeaturesResponse getMyEnabledFeatures(Long tenantId) {
        String tenantIdStr = String.valueOf(tenantId);

        List<String> enabledKeys = featureFlagRepository
                .findAllByOrderByCategoryAscFeatureNameAsc()
                .stream()
                .filter(flag -> flag.isEnabledForTenant(tenantIdStr))
                .map(FeatureFlag::getFeatureKey)
                .collect(Collectors.toList());

        return MyFeaturesResponse.builder()
                .tenantId(tenantId)
                .enabledFeatures(enabledKeys)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INTERNAL helpers
    // ─────────────────────────────────────────────────────────────────────────

    private FeatureFlag getByKey(String featureKey) {
        return featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new RuntimeException("Feature not found: " + featureKey));
    }

    private FeatureFlagDto toDto(FeatureFlag flag) {
        return FeatureFlagDto.builder()
                .id(flag.getId())
                .featureKey(flag.getFeatureKey())
                .featureName(flag.getFeatureName())
                .description(flag.getDescription())
                .category(flag.getCategory())
                .globallyEnabled(flag.getGloballyEnabled())
                .build();
    }
}
