package com.logistics.tracking.controller;

import com.logistics.tracking.dto.LocationUpdate;
import com.logistics.tracking.dto.LiveTrackingResponse;
import com.logistics.tracking.service.LiveTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TrackingController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;
    private final LiveTrackingService liveTrackingService;

    @GetMapping("/api/v1/tracking/{orderId}")
    public LiveTrackingResponse getInitialTrackingState(@PathVariable String orderId) {
        log.info("REST: Received request for tracking state on order {}", orderId);
        return liveTrackingService.getInitialTrackingState(orderId);
    }

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
