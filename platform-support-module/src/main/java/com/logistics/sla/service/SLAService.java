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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAService {

    private final SLARepository slaRepository;
    private final SLAInstanceRepository slaInstanceRepository;
    private final SLABreachRepository slaBreachRepository;
    private final NotificationServiceClient notificationClient;

    public List<SLA> getActiveSLAs(String entityType) {
        return slaRepository.findByEntityTypeAndIsActiveTrue(entityType);
    }

    @Transactional
    public void startSla(SLA sla, String entityId, LocalDateTime startTime) {
        Optional<SLAInstance> existing = slaInstanceRepository.findBySlaIdAndEntityId(sla.getId().toString(), entityId);
        if (existing.isPresent()) {
            log.info("SLA Instance already exists for SLA {} and Entity {}", sla.getName(), entityId);
            return;
        }

        SLAInstance instance = SLAInstance.builder()
                .slaId(sla.getId().toString())
                .entityId(entityId)
                .entityType(sla.getEntityType())
                .startTime(startTime)
                .isCompleted(false)
                .isBreached(false)
                .build();
        slaInstanceRepository.save(instance);
        log.info("Started SLA {} for Entity {}", sla.getName(), entityId);
    }

    @Transactional
    public void endSla(SLA sla, String entityId, LocalDateTime endTime) {
        Optional<SLAInstance> instanceOpt = slaInstanceRepository.findBySlaIdAndEntityId(sla.getId().toString(),
                entityId);
        if (instanceOpt.isEmpty()) {
            log.warn("No active SLA Instance found for SLA {} and Entity {}", sla.getName(), entityId);
            return;
        }

        SLAInstance instance = instanceOpt.get();
        if (instance.isCompleted()) {
            return;
        }

        instance.setEndTime(endTime);
        instance.setCompleted(true);

        long durationSeconds = Duration.between(instance.getStartTime(), endTime).getSeconds();
        if (durationSeconds > sla.getMaxDurationSeconds()) {
            instance.setBreached(true);
            recordBreach(sla, instance, durationSeconds);
        }

        slaInstanceRepository.save(instance);
        log.info("Ended SLA {} for Entity {}. Breached: {}", sla.getName(), entityId, instance.isBreached());
    }

    private void recordBreach(SLA sla, SLAInstance instance, long actualDuration) {
        SLABreach breach = SLABreach.builder()
                .slaId(sla.getId().toString())
                .entityId(instance.getEntityId())
                .entityType(instance.getEntityType())
                .actualDurationSeconds(actualDuration)
                .breachTime(LocalDateTime.now())
                .status("DETECTED")
                .build();
        slaBreachRepository.save(breach);

        // Trigger Notification
        try {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("slaId", sla.getId());
            metadata.put("slaName", sla.getName());
            metadata.put("entityId", instance.getEntityId());
            metadata.put("actualDuration", actualDuration);
            metadata.put("maxDuration", sla.getMaxDurationSeconds());

            notificationClient.sendNotification(NotificationServiceClient.SendNotificationRequest.builder()
                    .recipientId("SLA_ADMIN")
                    .recipientType("TEAM")
                    .channel("PUSH")
                    .subject("SLA Breach Detected: " + sla.getName())
                    .body(String.format("SLA %s breached for %s:%s. Actual: %ds, Max: %ds",
                            sla.getName(), instance.getEntityType(), instance.getEntityId(),
                            actualDuration, sla.getMaxDurationSeconds()))
                    .metadata(metadata)
                    .build());
            log.info("SLA breach notification sent for Entity {}", instance.getEntityId());
        } catch (Exception e) {
            log.error("Failed to send SLA breach notification for Entity {}: {}", instance.getEntityId(),
                    e.getMessage());
        }

        log.warn("SLA BREACH DETECTED: {} for Entity {}", sla.getName(), instance.getEntityId());
    }
}
