package com.logistics.audit.listener;

import com.logistics.audit.service.AuditLogService;
import com.logistics.platform.event.dto.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAuditListener {

    private final AuditLogService auditLogService;

    @KafkaListener(topics = "${audit.kafka.topic:audit-logs}", groupId = "${spring.kafka.consumer.group-id:audit-log-service}")
    public void consumeAuditLog(AuditLogEvent event) {
        log.info("Received audit log event: {}", event);
        try {
            auditLogService.recordAudit(
                    event.getEntityId(),
                    event.getEntityType(),
                    event.getAction(),
                    event.getChangedBy(),
                    event.getTenantId(),
                    event.getOldValue(),
                    event.getNewValue());
            log.debug("Saved audit log for entity: {}", event.getEntityId());
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", event, e);
        }
    }
}
