package com.logistics.onboarding.repository;

import com.logistics.onboarding.entity.TenantOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantOnboardingRepository extends JpaRepository<TenantOnboarding, Long> {
    
    Optional<TenantOnboarding> findByTenantId(Long tenantId);
    
    Optional<TenantOnboarding> findByCompanyEmail(String companyEmail);
    
    List<TenantOnboarding> findByStatus(TenantOnboarding.OnboardingStatus status);
    
    List<TenantOnboarding> findByIsTrialAndTrialEndDateBefore(Boolean isTrial, LocalDateTime date);
    
    List<TenantOnboarding> findByWelcomeEmailSentFalse();
    
    List<TenantOnboarding> findBySetupGuideEmailSentFalseAndCompanyInfoCompletedTrue();
}
