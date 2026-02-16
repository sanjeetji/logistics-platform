package com.logistics.exception.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.exception.dto.TrackingEventDto;
import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.repository.ExceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionEventListener {

    private final ExceptionRepository exceptionRepository;
    private final ObjectMapper objectMapper;

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

        exceptionRepository.save(record);
        // TODO: Trigger notification or control tower update
    }
}
