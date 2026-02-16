package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when onboarding starts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingStartedEvent {
    private Long tenantId;
    private String companyName;
    private String companyEmail;
    private String subscriptionPlan;
    private LocalDateTime trialStartDate;
    private LocalDateTime trialEndDate;
    private LocalDateTime eventTime;
}
