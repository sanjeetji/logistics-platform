package com.logistics.sla.service;

import com.logistics.sla.client.NotificationServiceClient;
import com.logistics.sla.model.SLA;
import com.logistics.sla.model.SLABreach;
import com.logistics.sla.model.SLAInstance;
import com.logistics.sla.repository.SLABreachRepository;
import com.logistics.sla.repository.SLAInstanceRepository;
import com.logistics.sla.repository.SLARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAMonitoringService {

    private final SLARepository slaRepository;
    private final SLAInstanceRepository instanceRepository;
    private final SLABreachRepository breachRepository;
    private final NotificationServiceClient notificationClient;

    /**
     * Scheduled job to monitor SLA breaches
     * Runs every 15 minutes
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void monitorSLABreaches() {
        log.info("Running SLA breach monitoring");

        // 1. Check active instances for current breaches
        List<SLAInstance> activeInstances = instanceRepository.findByIsCompletedFalse();
        LocalDateTime now = LocalDateTime.now();

        for (SLAInstance instance : activeInstances) {
            slaRepository.findById(Long.parseLong(instance.getSlaId())).ifPresent(sla -> {
                long durationSeconds = java.time.Duration.between(instance.getStartTime(), now).getSeconds();
                if (durationSeconds > sla.getMaxDurationSeconds() && !instance.isBreached()) {
                    log.warn("Active SLA Instance {} breached! Entity: {}", sla.getName(), instance.getEntityId());
                    instance.setBreached(true);
                    instanceRepository.save(instance);

                    recordNewBreach(sla, instance, durationSeconds);
                }
            });
        }

        // 2. Escalate unresolved breaches
        List<SLABreach> activeBreaches = breachRepository.findByResolvedFalse();
        for (SLABreach breach : activeBreaches) {
            escalateBreach(breach);
        }
    }

    private void recordNewBreach(SLA sla, SLAInstance instance, long actualDuration) {
        SLABreach breach = SLABreach.builder()
                .slaId(sla.getId().toString())
                .entityId(instance.getEntityId())
                .entityType(instance.getEntityType())
                .actualDurationSeconds(actualDuration)
                .breachTime(LocalDateTime.now())
                .status("DETECTED")
                .escalationLevel(0)
                .resolved(false)
                .build();
        breachRepository.save(breach);
    }

    private void escalateBreach(SLABreach breach) {
        LocalDateTime now = LocalDateTime.now();
        long minutesSinceBreach = java.time.Duration.between(breach.getBreachTime(), now).toMinutes();

        int oldLevel = breach.getEscalationLevel();
        int newLevel = oldLevel;

        // Escalation hierarchy: 15min -> L1, 30min -> L2, 60min -> L3
        if (minutesSinceBreach >= 60 && oldLevel < 3) {
            newLevel = 3;
        } else if (minutesSinceBreach >= 30 && oldLevel < 2) {
            newLevel = 2;
        } else if (minutesSinceBreach >= 15 && oldLevel < 1) {
            newLevel = 1;
        }

        if (newLevel > oldLevel) {
            breach.setEscalationLevel(newLevel);
            log.warn("SLA breach {} escalated to L{}", breach.getId(), newLevel);
            sendEscalationNotification(breach, newLevel);
            breachRepository.save(breach);
        }
    }

    private void sendEscalationNotification(SLABreach breach, int level) {
        try {
            String recipient = switch (level) {
                case 1 -> "SUPPORT_TEAM";
                case 2 -> "TEAM_LEAD";
                case 3 -> "SENIOR_MANAGEMENT";
                default -> "SLA_ADMIN";
            };

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("breachId", breach.getId());
            metadata.put("entityId", breach.getEntityId());
            metadata.put("escalationLevel", level);

            notificationClient.sendNotification(NotificationServiceClient.SendNotificationRequest.builder()
                    .recipientId(recipient)
                    .recipientType("TEAM")
                    .channel("EMAIL")
                    .subject(String.format("URGENT: SLA Escalation L%d - %s", level, breach.getEntityId()))
                    .body(String.format("SLA Breach for %s %s has reached escalation level %d.",
                            breach.getEntityType(), breach.getEntityId(), level))
                    .metadata(metadata)
                    .build());
            log.info("Escalation L{} notification sent for breach {}", level, breach.getId());
        } catch (Exception e) {
            log.error("Failed to send escalation notification for breach {}: {}", breach.getId(), e.getMessage());
        }
    }

    @Transactional
    public void resolveBreach(Long breachId, String resolvedBy, String resolution) {
        SLABreach breach = breachRepository.findById(breachId)
                .orElseThrow(() -> new RuntimeException("Breach not found"));

        breach.setResolved(true);
        breach.setResolvedAt(LocalDateTime.now());
        breach.setResolvedBy(resolvedBy);
        breach.setResolution(resolution);
        breach.setStatus("RESOLVED");

        breachRepository.save(breach);
        log.info("SLA breach {} resolved by {}", breachId, resolvedBy);
    }
}
