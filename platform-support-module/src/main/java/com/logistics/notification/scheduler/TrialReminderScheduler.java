package com.logistics.notification.scheduler;

import com.logistics.notification.service.OnboardingEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Scheduled job to send trial reminder emails
 */
@Slf4j
@Component
public class TrialReminderScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final OnboardingEmailService onboardingEmailService;

    public TrialReminderScheduler(JdbcTemplate jdbcTemplate,
            OnboardingEmailService onboardingEmailService) {
        this.jdbcTemplate = jdbcTemplate;
        this.onboardingEmailService = onboardingEmailService;
    }

    /**
     * Run daily at 9 AM to send trial reminders
     */
    @Scheduled(cron = "0 0 9 * * *") // 9 AM every day
    public void sendTrialReminders() {
        log.info("Starting trial reminder job");

        try {
            // Send 7-day reminders
            sendRemindersForDays(7);

            // Send 3-day reminders
            sendRemindersForDays(3);

            // Send 1-day reminders
            sendRemindersForDays(1);

            log.info("Trial reminder job completed successfully");
        } catch (Exception e) {
            log.error("Error in trial reminder job", e);
        }
    }

    private void sendRemindersForDays(int daysRemaining) {
        String sql = """
                SELECT tenant_id, company_name, company_email, trial_end_date
                FROM tenant_onboarding
                WHERE is_trial = true
                  AND trial_converted = false
                  AND status = 'IN_PROGRESS'
                  AND DATE(trial_end_date) = DATE(NOW() + INTERVAL '%d days')
                  AND CASE
                      WHEN ? = 7 THEN day7_checkin_sent = false
                      WHEN ? = 3 THEN day3_checkin_sent = false
                      WHEN ? = 1 THEN (day14_checkin_sent = false OR day14_checkin_sent IS NULL)
                      ELSE false
                  END
                """.formatted(daysRemaining);

        List<Map<String, Object>> tenants = jdbcTemplate.queryForList(sql, daysRemaining, daysRemaining, daysRemaining);

        log.info("Found {} tenants with {} days remaining", tenants.size(), daysRemaining);

        for (Map<String, Object> tenant : tenants) {
            try {
                String email = (String) tenant.get("company_email");
                String companyName = (String) tenant.get("company_name");
                Long tenantId = (Long) tenant.get("tenant_id");

                onboardingEmailService.sendTrialReminderEmail(email, companyName, daysRemaining);

                // Mark reminder as sent
                updateReminderSent(tenantId, daysRemaining);

                log.info("Sent {}-day reminder to tenant: {}", daysRemaining, tenantId);
            } catch (Exception e) {
                log.error("Failed to send reminder for tenant", e);
            }
        }
    }

    private void updateReminderSent(Long tenantId, int daysRemaining) {
        String column = switch (daysRemaining) {
            case 7 -> "day7_checkin_sent";
            case 3 -> "day3_checkin_sent";
            case 1 -> "day14_checkin_sent"; // Using day14 for 1-day reminder
            default -> null;
        };

        if (column != null) {
            String sql = "UPDATE tenant_onboarding SET " + column + " = true WHERE tenant_id = ?";
            jdbcTemplate.update(sql, tenantId);
        }
    }
}
