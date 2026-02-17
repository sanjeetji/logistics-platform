package com.logistics.exception.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.exception.dto.TrackingEventDto;
import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.repository.ExceptionRepository;
import com.logistics.platform.event.dto.ExceptionCreatedEvent;
import com.logistics.platform.event.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionEventListener {

    private final ExceptionRepository exceptionRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "tracking.events", groupId = "exception-management-group")
    public void listen(String message) {
        try {
            log.info("Received tracking event: {}", message);
            TrackingEventDto event = objectMapper.readValue(message, TrackingEventDto.class);

            if ("SLA_BREACH_PREDICTED".equals(event.getEventType())) {
                createException(event);
            }
        } catch (Exception e) {
            log.error("Error processing tracking event", e);
        }
    }

    private void createException(TrackingEventDto event) {
        log.warn("Creating exception record for order: {}", event.getOrderId());

        ExceptionRecord record = ExceptionRecord.builder()
                .orderId(event.getOrderId())
                .driverId(event.getDriverId())
                .type(event.getEventType())
                .severity("HIGH") // Default for SLA breach
                .description(event.getMessage())
                .timestamp(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now())
                .status("OPEN")
                .build();

        ExceptionRecord savedRecord = exceptionRepository.save(record);

        // Publish ExceptionCreatedEvent
        ExceptionCreatedEvent exceptionEvent = ExceptionCreatedEvent.builder()
                .id(savedRecord.getId())
                .orderId(savedRecord.getOrderId())
                .driverId(savedRecord.getDriverId())
                .type(savedRecord.getType())
                .severity(savedRecord.getSeverity())
                .description(savedRecord.getDescription())
                .timestamp(savedRecord.getTimestamp())
                .build();

        kafkaTemplate.send("exception.events", exceptionEvent);
        log.info("Published ExceptionCreatedEvent for order: {}", savedRecord.getOrderId());

        // Trigger Notification
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .recipient("OPS_MANAGER") // Placeholder, should be resolved based on rules
                .type("PUSH")
                .subject("SLA Breach Alert: " + savedRecord.getOrderId())
                .content("SLA Breach predicted for Order " + savedRecord.getOrderId() + ". "
                        + savedRecord.getDescription())
                .metaData(Map.of("orderId", savedRecord.getOrderId(), "severity", savedRecord.getSeverity()))
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("notification.events", notificationEvent);
        log.info("Published NotificationEvent for order: {}", savedRecord.getOrderId());
    }
}
