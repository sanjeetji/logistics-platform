package com.logistics.onboarding.service;

import com.logistics.onboarding.dto.*;
import com.logistics.onboarding.entity.TenantOnboarding;
import com.logistics.onboarding.repository.TenantOnboardingRepository;
import com.logistics.platform.event.dto.NotificationEvent;
import com.logistics.platform.event.dto.OnboardingStartedEvent;
import com.logistics.platform.event.dto.OnboardingCompletedEvent;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

        private final TenantOnboardingRepository onboardingRepository;
        private final StripeService stripeService;
        private final KafkaTemplate<String, Object> kafkaTemplate;

        @Value("${trial.duration-days:14}")
        private int trialDurationDays;

        @Value("${notification.topic:notification-events}")
        private String notificationTopic;

        /**
         * Start onboarding process
         */
        @Transactional
        public OnboardingResponse startOnboarding(StartOnboardingRequest request) {
                // Check if already exists
                if (onboardingRepository.findByCompanyEmail(request.getCompanyEmail()).isPresent()) {
                        throw new IllegalStateException("Company already registered with this email");
                }

                TenantOnboarding onboarding = TenantOnboarding.builder()
                                .companyName(request.getCompanyName())
                                .companyEmail(request.getCompanyEmail())
                                .companyPhone(request.getCompanyPhone())
                                .contactPersonName(request.getContactPersonName())
                                .contactPersonEmail(request.getContactPersonEmail())
                                .businessType(request.getBusinessType())
                                .industry(request.getIndustry())
                                .expectedMonthlyOrders(request.getExpectedMonthlyOrders())
                                .country(request.getCountry())
                                .city(request.getCity())
                                .status(TenantOnboarding.OnboardingStatus.IN_PROGRESS)
                                .currentStep(1)
                                .companyInfoCompleted(true)
                                .build();

                onboarding = onboardingRepository.save(onboarding);
                log.info("Started onboarding for company: {}", request.getCompanyName());

                // Publish OnboardingStartedEvent for email automation
                OnboardingStartedEvent startedEvent = OnboardingStartedEvent.builder()
                                .tenantId(onboarding.getId())
                                .companyName(onboarding.getCompanyName())
                                .companyEmail(onboarding.getCompanyEmail())
                                .subscriptionPlan(null) // Will be set during subscription setup
                                .trialStartDate(null) // Will be set during subscription setup
                                .trialEndDate(null)
                                .eventTime(LocalDateTime.now())
                                .build();
                kafkaTemplate.send("onboarding.started", onboarding.getId().toString(), startedEvent);

                // Mark welcome email as sent (will be sent by notification-service)
                onboarding.setWelcomeEmailSent(true);
                onboardingRepository.save(onboarding);

                // Publish tenant started event (for other services)
                kafkaTemplate.send("tenant-onboarding-started", onboarding.getId().toString(), onboarding);

                return mapToResponse(onboarding);
        }

        /**
         * Setup subscription with Stripe
         */
        @Transactional
        public OnboardingResponse setupSubscription(Long onboardingId, SubscriptionRequest request) {
                TenantOnboarding onboarding = onboardingRepository.findById(onboardingId)
                                .orElseThrow(() -> new IllegalArgumentException("Onboarding not found"));

                try {
                        // Create Stripe customer
                        Customer customer = stripeService.createCustomer(
                                        onboarding.getCompanyEmail(),
                                        onboarding.getContactPersonName(),
                                        onboarding.getCompanyName());
                        onboarding.setStripeCustomerId(customer.getId());

                        // Attach payment method
                        PaymentMethod paymentMethod = stripeService.attachPaymentMethod(
                                        customer.getId(),
                                        request.getPaymentMethodId());
                        onboarding.setStripePaymentMethodId(paymentMethod.getId());

                        // Create subscription
                        String priceId = stripeService.getPriceIdForPlan(request.getSubscriptionPlan());
                        boolean startTrial = request.getStartTrial() != null ? request.getStartTrial() : true;

                        Subscription subscription = stripeService.createSubscription(
                                        customer.getId(),
                                        priceId,
                                        startTrial,
                                        trialDurationDays);

                        onboarding.setStripeSubscriptionId(subscription.getId());
                        onboarding.setSubscriptionPlan(request.getSubscriptionPlan());
                        onboarding.setPaymentSetupCompleted(true);
                        onboarding.setCurrentStep(4);

                        // Setup trial if applicable
                        if (startTrial) {
                                onboarding.setIsTrial(true);
                                onboarding.setTrialStartDate(LocalDateTime.now());
                                onboarding.setTrialEndDate(LocalDateTime.now().plusDays(trialDurationDays));
                        }

                        onboarding = onboardingRepository.save(onboarding);
                        log.info("Setup subscription for onboarding: {}", onboardingId);

                        return mapToResponse(onboarding);

                } catch (Exception e) {
                        log.error("Error setting up subscription", e);
                        throw new RuntimeException("Failed to setup subscription", e);
                }
        }

        /**
         * Complete onboarding
         */
        @Transactional
        public OnboardingResponse completeOnboarding(Long onboardingId) {
                TenantOnboarding onboarding = onboardingRepository.findById(onboardingId)
                                .orElseThrow(() -> new IllegalArgumentException("Onboarding not found"));

                onboarding.setSetupCompleted(true);
                onboarding.setStatus(TenantOnboarding.OnboardingStatus.COMPLETED);
                onboarding.setCompletedAt(LocalDateTime.now());
                onboarding.setCurrentStep(onboarding.getTotalSteps());

                // Publish OnboardingCompletedEvent for email automation
                OnboardingCompletedEvent completedEvent = OnboardingCompletedEvent.builder()
                                .tenantId(onboarding.getId())
                                .companyName(onboarding.getCompanyName())
                                .companyEmail(onboarding.getCompanyEmail())
                                .completedAt(onboarding.getCompletedAt())
                                .eventTime(LocalDateTime.now())
                                .build();
                kafkaTemplate.send("onboarding.completed", onboarding.getId().toString(), completedEvent);

                onboarding = onboardingRepository.save(onboarding);
                log.info("Completed onboarding: {}", onboardingId);

                // Publish event
                // Publish event
                kafkaTemplate.send("tenant-onboarding-completed", onboarding.getId().toString(), onboarding);

                // Send Subscription Confirmation Email
                NotificationEvent subscriptionEmail = NotificationEvent.builder()
                                .recipient(onboarding.getCompanyEmail())
                                .type("EMAIL")
                                .subject("Subscription Confirmed")
                                .content(String.format(
                                                "Your subscription to %s plan is now active. You can start using the platform.",
                                                onboarding.getSubscriptionPlan()))
                                .timestamp(LocalDateTime.now())
                                .metaData(Map.of("plan", onboarding.getSubscriptionPlan()))
                                .build();
                kafkaTemplate.send(notificationTopic, onboarding.getCompanyEmail(), subscriptionEmail);

                return mapToResponse(onboarding);
        }

        /**
         * Get onboarding status
         */
        public OnboardingResponse getOnboarding(Long onboardingId) {
                TenantOnboarding onboarding = onboardingRepository.findById(onboardingId)
                                .orElseThrow(() -> new IllegalArgumentException("Onboarding not found"));

                return mapToResponse(onboarding);
        }

        /**
         * Map entity to response DTO
         */
        private OnboardingResponse mapToResponse(TenantOnboarding onboarding) {
                Integer daysRemaining = null;
                if (onboarding.getIsTrial() && onboarding.getTrialEndDate() != null) {
                        daysRemaining = (int) ChronoUnit.DAYS.between(LocalDateTime.now(),
                                        onboarding.getTrialEndDate());
                        daysRemaining = Math.max(0, daysRemaining);
                }

                return OnboardingResponse.builder()
                                .id(onboarding.getId())
                                .tenantId(onboarding.getTenantId())
                                .companyName(onboarding.getCompanyName())
                                .companyEmail(onboarding.getCompanyEmail())
                                .status(onboarding.getStatus().name())
                                .currentStep(onboarding.getCurrentStep())
                                .totalSteps(onboarding.getTotalSteps())
                                .companyInfoCompleted(onboarding.getCompanyInfoCompleted())
                                .serviceConfigCompleted(onboarding.getServiceConfigCompleted())
                                .paymentSetupCompleted(onboarding.getPaymentSetupCompleted())
                                .setupCompleted(onboarding.getSetupCompleted())
                                .subscriptionPlan(onboarding.getSubscriptionPlan())
                                .stripeCustomerId(onboarding.getStripeCustomerId())
                                .stripeSubscriptionId(onboarding.getStripeSubscriptionId())
                                .isTrial(onboarding.getIsTrial())
                                .trialStartDate(onboarding.getTrialStartDate())
                                .trialEndDate(onboarding.getTrialEndDate())
                                .daysRemainingInTrial(daysRemaining)
                                .createdAt(onboarding.getCreatedAt())
                                .updatedAt(onboarding.getUpdatedAt())
                                .completedAt(onboarding.getCompletedAt())
                                .build();
        }
}
