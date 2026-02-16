package com.logistics.tracking.controller;

import com.logistics.tracking.dto.LocationUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TrackingController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @MessageMapping("/track/{orderId}")
    @SendTo("/topic/location/{orderId}")
    public LocationUpdate updateLocation(@DestinationVariable String orderId, LocationUpdate locationUpdate) {
        log.info("Received location update for order {}: {}, {}", orderId, locationUpdate.getLatitude(),
                locationUpdate.getLongitude());

        locationUpdate.setTimestamp(LocalDateTime.now());

        // Persist latest location to Redis for initial load
        redisTemplate.opsForValue().set("location:" + orderId, locationUpdate);

        // Publish to Kafka for fleet-service and other consumers
        publishToKafka(orderId, locationUpdate);

        return locationUpdate;
    }

    private void publishToKafka(String orderId, LocationUpdate update) {
        try {
            com.logistics.platform.event.dto.DriverLocationUpdatedEvent event = com.logistics.platform.event.dto.DriverLocationUpdatedEvent
                    .builder()
                    .driverId(update.getDriverId())
                    .latitude(update.getLatitude())
                    .longitude(update.getLongitude())
                    .orderId(orderId)
                    .timestamp(update.getTimestamp())
                    .build();

            kafkaTemplate.send("driver-location-updates", update.getDriverId(), event);
        } catch (Exception e) {
            log.error("Failed to publish location update to Kafka", e);
        }
    }
}
