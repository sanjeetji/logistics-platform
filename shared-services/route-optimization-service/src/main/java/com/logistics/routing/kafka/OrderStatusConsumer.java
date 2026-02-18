package com.logistics.routing.kafka;

import com.logistics.platform.event.dto.OrderStatusChangedEvent;
import com.logistics.routing.ml.ETAFeedbackService;
import com.logistics.routing.model.ETAPrediction;
import com.logistics.routing.repository.ETAPredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusConsumer {

    private final ETAPredictionRepository etaPredictionRepository;
    private final ETAFeedbackService etaFeedbackService;

    @KafkaListener(topics = "platform.orders.status", groupId = "route-optimization-feedback-group")
    public void handleOrderStatusChange(OrderStatusChangedEvent event) {
        if ("DELIVERED".equals(event.getNewStatus())) {
            processDeliveryFeedback(event);
        }
    }

    private void processDeliveryFeedback(OrderStatusChangedEvent event) {
        try {
            Optional<ETAPrediction> predictionOpt = etaPredictionRepository.findById(event.getOrderId());

            if (predictionOpt.isPresent()) {
                ETAPrediction prediction = predictionOpt.get();

                // Event timestamp is LocalDateTime in BaseEvent
                LocalDateTime actualDateTime = event.getTimestamp();
                if (actualDateTime == null) {
                    actualDateTime = java.time.LocalDateTime.now();
                }
                long actualTimestamp = actualDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                // Convert LocalDateTime to epoch millis
                long predictedTimestamp = prediction.getPredictedArrival()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();

                long errorSeconds = (actualTimestamp - predictedTimestamp) / 1000;

                ETAFeedbackEvent feedback = ETAFeedbackEvent.builder()
                        .feedbackId(UUID.randomUUID().toString())
                        .deliveryId(event.getOrderId())
                        .routeId(prediction.getRouteId())
                        .predictionTimestamp(
                                prediction.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .actualArrivalTimestamp(actualTimestamp)
                        .predictedDurationSeconds(predictedTimestamp / 1000)
                        .actualDurationSeconds(actualTimestamp / 1000)
                        .errorSeconds(errorSeconds)
                        .build();

                etaFeedbackService.sendFeedback(feedback);

                // Clean up prediction
                etaPredictionRepository.delete(prediction);
            }
        } catch (Exception e) {
            log.error("Error processing delivery feedback for order {}", event.getOrderId(), e);
        }
    }
}
