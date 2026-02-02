package com.logistics.tenant.service;

import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Tenant createTenant(Tenant tenant) {
        if (tenantRepository.findByDomain(tenant.getDomain()).isPresent()) {
            throw new RuntimeException("Tenant with domain already exists: " + tenant.getDomain());
        }
        return tenantRepository.save(tenant);
    }

    public Tenant updateTenant(Long id, Tenant details) {
        Tenant tenant = getTenantById(id);
        tenant.setName(details.getName());
        tenant.setDomain(details.getDomain());
        tenant.setIndustryType(details.getIndustryType());
        tenant.setSubscriptionPlan(details.getSubscriptionPlan());
        tenant.setActive(details.isActive());
        return tenantRepository.save(tenant);
    }
}
