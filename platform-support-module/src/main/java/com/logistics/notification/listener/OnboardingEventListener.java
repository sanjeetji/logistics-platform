package com.logistics.notification.listener;

import com.logistics.notification.service.OnboardingEmailService;
import com.logistics.platform.event.dto.OnboardingCompletedEvent;
import com.logistics.platform.event.dto.OnboardingStartedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for onboarding events
 */
@Slf4j
@Component
public class OnboardingEventListener {

    private final OnboardingEmailService onboardingEmailService;

    public OnboardingEventListener(OnboardingEmailService onboardingEmailService) {
        this.onboardingEmailService = onboardingEmailService;
    }

    /**
     * Listen to onboarding started events and send welcome email
     */
    @KafkaListener(topics = "onboarding.started", groupId = "notification-service")
    public void handleOnboardingStarted(OnboardingStartedEvent event) {
        log.info("Received onboarding started event for tenant: {}", event.getTenantId());

        try {
            onboardingEmailService.sendWelcomeEmail(
                    event.getCompanyEmail(),
                    event.getCompanyName(),
                    event.getTenantId());
            log.info("Welcome email sent successfully for tenant: {}", event.getTenantId());
        } catch (Exception e) {
            log.error("Failed to send welcome email for tenant: {}", event.getTenantId(), e);
            // In production, implement retry logic or dead letter queue
        }
    }

    /**
     * Listen to onboarding completed events and send completion email
     */
    @KafkaListener(topics = "onboarding.completed", groupId = "notification-service")
    public void handleOnboardingCompleted(OnboardingCompletedEvent event) {
        log.info("Received onboarding completed event for tenant: {}", event.getTenantId());

        try {
            onboardingEmailService.sendOnboardingCompleteEmail(
                    event.getCompanyEmail(),
                    event.getCompanyName());
            log.info("Onboarding complete email sent successfully for tenant: {}", event.getTenantId());
        } catch (Exception e) {
            log.error("Failed to send onboarding complete email for tenant: {}", event.getTenantId(), e);
        }
    }
}
