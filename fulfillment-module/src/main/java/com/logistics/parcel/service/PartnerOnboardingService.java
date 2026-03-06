package com.logistics.parcel.service;

import com.logistics.parcel.model.Partner;
import com.logistics.parcel.model.PartnerOnboarding;
import com.logistics.parcel.repository.PartnerOnboardingRepository;
import com.logistics.parcel.repository.PartnerRepository;
import com.logistics.platform.utils.tenant.TenantContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerOnboardingService {

    private final PartnerOnboardingRepository onboardingRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerOnboarding initiateOnboarding(String partnerName, String contactEmail) {
        PartnerOnboarding onboarding = PartnerOnboarding.builder()
                .partnerName(partnerName)
                .contactEmail(contactEmail)
                .status(PartnerOnboarding.OnboardingStatus.PENDING)
                .tenantId(TenantContextUtils.getTenantId())
                .build();

        return onboardingRepository.save(onboarding);
    }

    @Transactional
    public PartnerOnboarding verifyPartner(Long onboardingId, String details) {
        PartnerOnboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new RuntimeException("Onboarding not found"));

        onboarding.setStatus(PartnerOnboarding.OnboardingStatus.VERIFYING);
        onboarding.setVerificationDetails(details);

        return onboardingRepository.save(onboarding);
    }

    @Transactional
    public Partner completeOnboarding(Long onboardingId) {
        PartnerOnboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new RuntimeException("Onboarding not found"));

        onboarding.setStatus(PartnerOnboarding.OnboardingStatus.ACTIVE);
        onboarding.setVerifiedAt(LocalDateTime.now());
        onboardingRepository.save(onboarding);

        Partner partner = Partner.builder()
                .name(onboarding.getPartnerName())
                .status("ACTIVE")
                .tenantId(onboarding.getTenantId())
                .build();

        return partnerRepository.save(partner);
    }
}
