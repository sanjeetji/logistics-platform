package com.logistics.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Business logic for onboarding email notifications
 */
@Slf4j
@Service
public class OnboardingEmailService {

    private final EmailService emailService;

    public OnboardingEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Send welcome email immediately after onboarding starts
     */
    public void sendWelcomeEmail(String recipientEmail, String companyName, Long tenantId) {
        log.info("Sending welcome email to: {} for company: {}", recipientEmail, companyName);

        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", companyName);
        variables.put("tenantId", tenantId);
        variables.put("dashboardUrl", "https://platform.logistics.com/dashboard");
        variables.put("supportEmail", "support@logistics-platform.com");

        emailService.sendTemplateEmail(
                recipientEmail,
                "welcome-email",
                variables,
                "Welcome to Logistics Platform - Let's Get Started!");
    }

    /**
     * Send setup guide email (Day 1)
     */
    public void sendSetupGuideEmail(String recipientEmail, String companyName) {
        log.info("Sending setup guide email to: {}", recipientEmail);

        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", companyName);
        variables.put("setupGuideUrl", "https://platform.logistics.com/setup-guide");
        variables.put("videoTutorialUrl", "https://platform.logistics.com/tutorials");
        variables.put("supportEmail", "support@logistics-platform.com");

        emailService.sendTemplateEmail(
                recipientEmail,
                "setup-guide-email",
                variables,
                "Your Setup Guide - Get the Most Out of Logistics Platform");
    }

    /**
     * Send trial reminder email
     */
    public void sendTrialReminderEmail(String recipientEmail, String companyName, int daysRemaining) {
        log.info("Sending trial reminder email to: {} ({} days remaining)", recipientEmail, daysRemaining);

        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", companyName);
        variables.put("daysRemaining", daysRemaining);
        variables.put("upgradeUrl", "https://platform.logistics.com/upgrade");
        variables.put("pricingUrl", "https://platform.logistics.com/pricing");

        String subject = daysRemaining == 1
                ? "Your Trial Ends Tomorrow - Upgrade to Continue"
                : String.format("Your Trial Ends in %d Days - Don't Miss Out!", daysRemaining);

        emailService.sendTemplateEmail(
                recipientEmail,
                "trial-reminder-email",
                variables,
                subject);
    }

    /**
     * Send onboarding completion email
     */
    public void sendOnboardingCompleteEmail(String recipientEmail, String companyName) {
        log.info("Sending onboarding complete email to: {}", recipientEmail);

        Map<String, Object> variables = new HashMap<>();
        variables.put("companyName", companyName);
        variables.put("advancedFeaturesUrl", "https://platform.logistics.com/advanced-features");
        variables.put("communityUrl", "https://community.logistics-platform.com");
        variables.put("supportEmail", "support@logistics-platform.com");

        emailService.sendTemplateEmail(
                recipientEmail,
                "onboarding-complete-email",
                variables,
                "🎉 Congratulations! Your Setup is Complete");
    }
}
