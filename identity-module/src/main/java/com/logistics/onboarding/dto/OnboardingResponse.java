package com.logistics.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponse {
    
    private Long id;
    private Long tenantId;
    private String companyName;
    private String companyEmail;
    private String status;
    private Integer currentStep;
    private Integer totalSteps;
    
    private Boolean companyInfoCompleted;
    private Boolean serviceConfigCompleted;
    private Boolean paymentSetupCompleted;
    private Boolean setupCompleted;
    
    // Subscription
    private String subscriptionPlan;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    
    // Trial
    private Boolean isTrial;
    private LocalDateTime trialStartDate;
    private LocalDateTime trialEndDate;
    private Integer daysRemainingInTrial;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
