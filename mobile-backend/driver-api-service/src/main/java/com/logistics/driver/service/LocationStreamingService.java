package com.logistics.driver.service;

import com.logistics.driver.dto.LocationUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationStreamingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, LocationUpdate> kafkaTemplate;

    private static final String KAFKA_TOPIC = "driver-location-updates";

    /**
     * Process location update from driver
     * 1. Publish to WebSocket for real-time tracking
     * 2. Publish to Kafka for persistence and analytics
     */
    public void processLocationUpdate(LocationUpdate locationUpdate) {
        locationUpdate.setTimestamp(LocalDateTime.now());

        // Publish to WebSocket for real-time subscribers
        publishToWebSocket(locationUpdate);

        // Publish to Kafka for persistence and downstream processing
        publishToKafka(locationUpdate);

        log.debug("Processed location update for driver: {}", locationUpdate.getDriverId());
    }

    private void publishToWebSocket(LocationUpdate locationUpdate) {
        try {
            // Broadcast to all subscribers of this driver's location
            messagingTemplate.convertAndSend(
                    "/topic/driver/" + locationUpdate.getDriverId() + "/location",
                    locationUpdate);

            // If driver is on an order, also publish to order-specific topic
            if (locationUpdate.getOrderId() != null) {
                messagingTemplate.convertAndSend(
                        "/topic/order/" + locationUpdate.getOrderId() + "/driver-location",
                        locationUpdate);
            }
        } catch (Exception e) {
            log.error("Failed to publish location to WebSocket", e);
        }
    }

    private void publishToKafka(LocationUpdate locationUpdate) {
        try {
            kafkaTemplate.send(KAFKA_TOPIC, locationUpdate.getDriverId(), locationUpdate);
        } catch (Exception e) {
            log.error("Failed to publish location to Kafka", e);
        }
    }

    /**
     * Notify subscribers about driver status change
     */
    public void broadcastDriverStatus(String driverId, String status, String orderId) {
        try {
            messagingTemplate.convertAndSend(
                    "/topic/driver/" + driverId + "/status",
                    new DriverStatusUpdate(driverId, status, orderId, LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Failed to broadcast driver status", e);
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class DriverStatusUpdate {
        private String driverId;
        private String status;
        private String orderId;
        private LocalDateTime timestamp;
    }
}
