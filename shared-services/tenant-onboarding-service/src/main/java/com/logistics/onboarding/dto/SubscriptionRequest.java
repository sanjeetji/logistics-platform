package com.logistics.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    
    @NotBlank(message = "Subscription plan is required")
    private String subscriptionPlan; // STARTER, GROWTH, ENTERPRISE
    
    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId; // Stripe payment method ID
    
    private Boolean startTrial;
}
