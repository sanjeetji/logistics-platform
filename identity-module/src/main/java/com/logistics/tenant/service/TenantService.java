package com.logistics.tenant.service;

import com.logistics.platform.common.dto.enums.BusinessModel;
import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.model.TenantConfig;
import com.logistics.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
    }

    @Transactional
    public Tenant createTenant(Tenant tenant) {
        if (tenantRepository.findByDomain(tenant.getDomain()).isPresent()) {
            throw new RuntimeException("Tenant with domain already exists: " + tenant.getDomain());
        }

        // Set default config if missing
        if (tenant.getConfig() == null) {
            tenant.setConfig(TenantConfig.builder()
                    .currency("USD")
                    .timezone("UTC")
                    .build());
        }

        // Set default business model if missing
        if (tenant.getBusinessModel() == null) {
            tenant.setBusinessModel(BusinessModel.B2B);
        }

        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant updateTenant(Long id, Tenant details) {
        Tenant tenant = getTenantById(id);
        tenant.setName(details.getName());
        tenant.setDomain(details.getDomain());
        tenant.setIndustryType(details.getIndustryType());
        tenant.setSubscriptionTier(details.getSubscriptionTier());
        tenant.setBusinessModel(details.getBusinessModel());
        tenant.setActive(details.isActive());
        tenant.setParentId(details.getParentId());
        tenant.setDataResidencyRegion(details.getDataResidencyRegion());

        if (details.getConfig() != null) {
            tenant.setConfig(details.getConfig());
        }

        return tenantRepository.save(tenant);
    }

    @Transactional
    public void updateTenantSetting(Long tenantId, String key, String value) {
        Tenant tenant = getTenantById(tenantId);
        if (tenant.getConfig() == null) {
            tenant.setConfig(new TenantConfig());
        }
        tenant.getConfig().getSettings().put(key, value);
        tenantRepository.save(tenant);
    }

    public String getTenantSetting(Long tenantId, String key, String defaultValue) {
        Tenant tenant = getTenantById(tenantId);
        if (tenant.getConfig() == null || tenant.getConfig().getSettings() == null) {
            return defaultValue;
        }
        return tenant.getConfig().getSettings().getOrDefault(key, defaultValue);
    }

    public List<Tenant> getChildTenants(Long parentId) {
        return tenantRepository.findByParentId(parentId);
    }

    public List<Tenant> getTenantHierarchy(Long tenantId) {
        List<Tenant> hierarchy = new ArrayList<>();
        Tenant current = getTenantById(tenantId);
        while (current != null) {
            hierarchy.add(0, current); // Add to beginning to get root -> child order
            if (current.getParentId() != null) {
                current = getTenantById(current.getParentId());
            } else {
                current = null;
            }
        }
        return hierarchy;
    }
}
