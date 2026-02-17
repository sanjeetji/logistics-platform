package com.logistics.controltower.listener;

import com.logistics.platform.event.dto.ExceptionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "tracking.events", groupId = "control-tower-group")
    public void consumeTrackingEvents(String message) {
        try {
            // Check if message is critical (e.g., contains SLA_BREACH)
            if (message.contains("SLA_BREACH_PREDICTED")) {
                log.info("Alert received: {}", message);
                // Push to /topic/alerts
                messagingTemplate.convertAndSend("/topic/alerts", message);
            }
        } catch (Exception e) {
            log.error("Error processing alert", e);
        }
    }

    @KafkaListener(topics = "exception.events", groupId = "control-tower-exception-group")
    public void consumeExceptionEvents(ExceptionCreatedEvent event) {
        try {
            log.info("Exception event received: {}", event);
            // Push to /topic/exceptions
            messagingTemplate.convertAndSend("/topic/exceptions", event);
        } catch (Exception e) {
            log.error("Error processing exception event", e);
        }
    }
}
