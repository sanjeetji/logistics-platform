package com.logistics.parcel.repository;

import com.logistics.parcel.model.PartnerOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerOnboardingRepository extends JpaRepository<PartnerOnboarding, Long> {
    List<PartnerOnboarding> findByTenantId(String tenantId);

    List<PartnerOnboarding> findByStatus(PartnerOnboarding.OnboardingStatus status);
}
