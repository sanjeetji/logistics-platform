package com.logistics.tenant.service;

import com.logistics.tenant.model.FeatureFlag;
import com.logistics.tenant.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;

    @Cacheable(value = "featureFlags", key = "#featureKey + ':' + #tenantId")
    public boolean isFeatureEnabled(String featureKey, String tenantId) {
        return featureFlagRepository.findByFeatureKey(featureKey)
                .map(flag -> flag.isEnabledForTenant(tenantId))
                .orElse(false);
    }

    @Transactional
    public void enableFeatureForTenant(String featureKey, String tenantId) {
        FeatureFlag flag = featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new RuntimeException("Feature not found: " + featureKey));
        flag.enableForTenant(tenantId);
        featureFlagRepository.save(flag);
        log.info("Enabled feature {} for tenant {}", featureKey, tenantId);
    }

    @Transactional
    public void disableFeatureForTenant(String featureKey, String tenantId) {
        FeatureFlag flag = featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new RuntimeException("Feature not found: " + featureKey));
        flag.disableForTenant(tenantId);
        featureFlagRepository.save(flag);
        log.info("Disabled feature {} for tenant {}", featureKey, tenantId);
    }

    @Transactional
    public FeatureFlag createFeatureFlag(String featureKey, String featureName, String description,
            boolean globallyEnabled) {
        FeatureFlag flag = FeatureFlag.builder()
                .featureKey(featureKey)
                .featureName(featureName)
                .description(description)
                .globallyEnabled(globallyEnabled)
                .build();
        return featureFlagRepository.save(flag);
    }
}
