package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when onboarding completes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingCompletedEvent {
    private Long tenantId;
    private String companyName;
    private String companyEmail;
    private LocalDateTime completedAt;
    private LocalDateTime eventTime;
}
