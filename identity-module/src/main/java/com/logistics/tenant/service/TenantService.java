package com.logistics.tenant.service;

import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.model.TenantConfig;
import com.logistics.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant updateTenant(Long id, Tenant details) {
        Tenant tenant = getTenantById(id);
        tenant.setName(details.getName());
        tenant.setDomain(details.getDomain());
        tenant.setIndustryType(details.getIndustryType());
        tenant.setSubscriptionTier(details.getSubscriptionTier());
        tenant.setActive(details.isActive());

        if (details.getConfig() != null) {
            tenant.setConfig(details.getConfig());
        }

        return tenantRepository.save(tenant);
    }
}
