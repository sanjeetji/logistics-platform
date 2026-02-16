package com.logistics.analytics.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.analytics.model.DeliveryPerformance;
import com.logistics.analytics.repository.DeliveryPerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;

@SuppressWarnings("unchecked")

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryPerformanceConsumer {

    private final DeliveryPerformanceRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "delivery.completed", groupId = "analytics-performance-group")
    public void consumeDeliveryCompleted(String message) {
        try {
            log.info("Processing delivery completion for performance analysis");
            // Assuming message is a map or DTO. Using map for flexibility here or create
            // DTO
            Map<String, Object> event = objectMapper.readValue(message, Map.class);

            String orderId = (String) event.get("orderId");
            // Extract other fields safely...

            DeliveryPerformance performance = DeliveryPerformance.builder()
                    .orderId(orderId)
                    .actualTime(LocalDateTime.now()) // Or extract from event
                    // Populate other fields
                    .build();

            repository.save(java.util.Objects.requireNonNull(performance));
            log.info("Saved delivery performance for order: {}", orderId);
        } catch (Exception e) {
            log.error("Error processing delivery performance event", e);
        }
    }
}
