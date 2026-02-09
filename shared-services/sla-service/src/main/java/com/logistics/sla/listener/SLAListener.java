package com.logistics.sla.listener;

import com.logistics.platform.common.dto.event.AuditLogEvent;
import com.logistics.sla.model.SLA;
import com.logistics.sla.service.SLAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SLAListener {

    private final SLAService slaService;

    @KafkaListener(topics = "${audit.kafka.topic:audit-logs}", groupId = "${spring.kafka.consumer.group-id:sla-service}")
    public void consumeAuditLog(AuditLogEvent event) {
        try {
            // Generic check for all active SLAs matching this entity type
            List<SLA> slas = slaService.getActiveSLAs(event.getEntityType());
            
            for (SLA sla : slas) {
                if (event.getAction().equalsIgnoreCase(sla.getStartEvent())) {
                    slaService.startSla(sla, event.getEntityId(), event.getTimestamp());
                } else if (event.getAction().equalsIgnoreCase(sla.getEndEvent())) {
                    slaService.endSla(sla, event.getEntityId(), event.getTimestamp());
                }
            }
        } catch (Exception e) {
            log.error("Error processing SLA check for event: {}", event, e);
        }
    }
}
