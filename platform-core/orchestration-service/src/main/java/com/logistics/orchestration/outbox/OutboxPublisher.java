package com.logistics.orchestration.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000) // Poll every 2 seconds
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findByProcessedAtIsNullOrderByCreatedAtAsc();
        
        for (OutboxEvent event : events) {
            try {
                log.info("Publishing Outbox Event ID: {} to Topic: {}", event.getId(), event.getTopic());
                
                // Assuming payload is JSON string, we send it as is or parse it based on requirement
                // Ideally, we deserialize to specific DTO if needed, but sending JSON string is generic
                kafkaTemplate.send(event.getTopic(), event.getPayload());

                event.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(event);
                
            } catch (Exception e) {
                log.error("Failed to publish Outbox Event ID: {}", event.getId(), e);
            }
        }
    }
}
