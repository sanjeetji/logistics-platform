package com.logistics.onboarding.service;

import com.logistics.onboarding.dto.OnboardingResponse;
import com.logistics.onboarding.dto.StartOnboardingRequest;
import com.logistics.onboarding.entity.TenantOnboarding;
import com.logistics.onboarding.repository.TenantOnboardingRepository;
import com.logistics.platform.event.dto.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private TenantOnboardingRepository onboardingRepository;

    @Mock
    private StripeService stripeService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OnboardingService onboardingService;

    @Captor
    private ArgumentCaptor<NotificationEvent> notificationEventCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(onboardingService, "trialDurationDays", 14);
        ReflectionTestUtils.setField(onboardingService, "notificationTopic", "notification-events");
    }

    @Test
    void startOnboarding_ShouldPublishWelcomeEmail() {
        // Arrange
        StartOnboardingRequest request = new StartOnboardingRequest();
        request.setCompanyName("Test Corp");
        request.setCompanyEmail("test@testcorp.com");

        when(onboardingRepository.findByCompanyEmail(request.getCompanyEmail())).thenReturn(Optional.empty());
        when(onboardingRepository.save(any(TenantOnboarding.class))).thenAnswer(invocation -> {
            TenantOnboarding t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        OnboardingResponse response = onboardingService.startOnboarding(request);

        // Assert
        assertNotNull(response);
        verify(kafkaTemplate).send(eq("tenant-onboarding-started"), any(String.class), any(TenantOnboarding.class));

        verify(kafkaTemplate).send(eq("notification-events"), eq("test@testcorp.com"),
                notificationEventCaptor.capture());
        NotificationEvent event = notificationEventCaptor.getValue();
        assertEquals("EMAIL", event.getType());
        assertEquals("Welcome to Logistics Platform", event.getSubject());
        assertEquals("test@testcorp.com", event.getRecipient());
    }

    @Test
    void completeOnboarding_ShouldPublishSubscriptionEmail() {
        // Arrange
        Long onboardingId = 1L;
        TenantOnboarding onboarding = new TenantOnboarding();
        onboarding.setId(onboardingId);
        onboarding.setCompanyEmail("test@testcorp.com");
        onboarding.setSubscriptionPlan("GROWTH");
        onboarding.setTotalSteps(4);

        when(onboardingRepository.findById(onboardingId)).thenReturn(Optional.of(onboarding));
        when(onboardingRepository.save(any(TenantOnboarding.class))).thenReturn(onboarding);

        // Act
        OnboardingResponse response = onboardingService.completeOnboarding(onboardingId);

        // Assert
        assertNotNull(response);
        verify(kafkaTemplate).send(eq("tenant-onboarding-completed"), any(String.class), any(TenantOnboarding.class));

        verify(kafkaTemplate).send(eq("notification-events"), eq("test@testcorp.com"),
                notificationEventCaptor.capture());
        NotificationEvent event = notificationEventCaptor.getValue();
        assertEquals("EMAIL", event.getType());
        assertEquals("Subscription Confirmed", event.getSubject());
        assertEquals("test@testcorp.com", event.getRecipient());
    }
}
