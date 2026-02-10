package com.logistics.tenant.repository;

import com.logistics.tenant.model.TenantUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TenantUsageRepository extends JpaRepository<TenantUsage, Long> {
    Optional<TenantUsage> findByTenantIdAndMonth(String tenantId, LocalDate month);
}
