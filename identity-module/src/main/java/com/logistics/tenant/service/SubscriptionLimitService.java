package com.logistics.tenant.service;

import com.logistics.tenant.model.SubscriptionTier;
import com.logistics.tenant.model.Tenant;
import com.logistics.tenant.model.TenantUsage;
import com.logistics.tenant.repository.TenantRepository;
import com.logistics.tenant.repository.TenantUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionLimitService {

    private final TenantRepository tenantRepository;
    private final TenantUsageRepository tenantUsageRepository;

    public boolean canCreateOrder(String tenantId) {
        Tenant tenant = tenantRepository.findById(Long.parseLong(tenantId))
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        SubscriptionTier tier = tenant.getSubscriptionTier();
        if (tier.isUnlimited("orders")) {
            return true;
        }

        TenantUsage usage = getCurrentMonthUsage(tenantId);
        return usage.getOrdersCreated() < tier.getMonthlyOrderLimit();
    }

    public boolean canAddDriver(String tenantId) {
        Tenant tenant = tenantRepository.findById(Long.parseLong(tenantId))
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        SubscriptionTier tier = tenant.getSubscriptionTier();
        if (tier.isUnlimited("drivers")) {
            return true;
        }

        TenantUsage usage = getCurrentMonthUsage(tenantId);
        return usage.getActiveDrivers() < tier.getMaxDrivers();
    }

    public boolean canAddVehicle(String tenantId) {
        Tenant tenant = tenantRepository.findById(Long.parseLong(tenantId))
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        SubscriptionTier tier = tenant.getSubscriptionTier();
        if (tier.isUnlimited("vehicles")) {
            return true;
        }

        TenantUsage usage = getCurrentMonthUsage(tenantId);
        return usage.getActiveVehicles() < tier.getMaxVehicles();
    }

    public void recordOrderCreation(String tenantId) {
        TenantUsage usage = getCurrentMonthUsage(tenantId);
        usage.incrementOrders();
        tenantUsageRepository.save(usage);
        log.debug("Recorded order creation for tenant {}", tenantId);
    }

    private TenantUsage getCurrentMonthUsage(String tenantId) {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        return tenantUsageRepository.findByTenantIdAndMonth(tenantId, currentMonth)
                .orElseGet(() -> {
                    TenantUsage newUsage = TenantUsage.builder()
                            .tenantId(tenantId)
                            .month(currentMonth)
                            .build();
                    return tenantUsageRepository.save(newUsage);
                });
    }
}
