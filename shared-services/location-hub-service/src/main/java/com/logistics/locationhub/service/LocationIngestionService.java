package com.logistics.locationhub.service;

import com.logistics.locationhub.dto.LocationUpdateDTO;
import com.logistics.platform.event.dto.DriverLocationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;

import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationIngestionService {

    private final RedisOperations<String, Object> redisTemplate;
    private final KafkaOperations<String, Object> kafkaTemplate;

    private static final String LOCATION_KEY_PREFIX = "driver:location:";
    private static final double MIN_DISTANCE_METERS = 10.0;
    private static final long MIN_TIME_SECONDS = 2;

    public void ingestLocation(LocationUpdateDTO update) {
        if (update.getTimestamp() == null) {
            update.setTimestamp(LocalDateTime.now());
        }

        // Check if we should ingest this update (smoothing & throttling)
        LocationUpdateDTO lastLocation = (LocationUpdateDTO) redisTemplate.opsForValue()
                .get(LOCATION_KEY_PREFIX + update.getDriverId());

        if (!shouldIngest(update, lastLocation)) {
            log.debug("Skipping location update for driver {} (Jitter/Throttled)", update.getDriverId());
            return;
        }

        // 1. Update Redis for fast spatial lookups and current state
        try {
            redisTemplate.opsForValue().set(LOCATION_KEY_PREFIX + update.getDriverId(), update);
        } catch (Exception e) {
            log.error("Failed to update Redis for driver {}", update.getDriverId(), e);
        }

        // 2. Publish to Kafka
        publishToKafka(update);
    }

    private boolean shouldIngest(LocationUpdateDTO newUpdate, LocationUpdateDTO lastUpdate) {
        if (lastUpdate == null) {
            return true;
        }

        // Calculate time difference
        long secondsDiff = java.time.Duration.between(lastUpdate.getTimestamp(), newUpdate.getTimestamp()).abs()
                .getSeconds();

        // Calculate distance (Haversine)
        double distance = calculateDistanceInMeters(
                lastUpdate.getLatitude(), lastUpdate.getLongitude(),
                newUpdate.getLatitude(), newUpdate.getLongitude());

        // Logic:
        // 1. If distance < MIN_DISTANCE_METERS (10m), treat as jitter -> Skip
        // 2. If time < MIN_TIME_SECONDS (2s) AND distance < 100m (not moving super
        // fast) -> Skip (Throttle)

        if (distance < MIN_DISTANCE_METERS) {
            return false;
        }

        if (secondsDiff < MIN_TIME_SECONDS && distance < 100) {
            return false;
        }

        return true;
    }

    private double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }

    private void publishToKafka(LocationUpdateDTO update) {
        try {
            DriverLocationUpdatedEvent event = DriverLocationUpdatedEvent.builder()
                    .driverId(update.getDriverId())
                    .orderId(update.getOrderId())
                    .latitude(update.getLatitude())
                    .longitude(update.getLongitude())
                    .accuracy(update.getAccuracy())
                    .speed(update.getSpeed())
                    .heading(update.getBearing())
                    .build();
            // Timestamp is now set in BaseEvent constructor or can be set explicitly if
            // needed, but BaseEvent defaults to now()
            // If we want to preserve the exact timestamp from the update:
            event.setTimestamp(update.getTimestamp());

            kafkaTemplate.send("driver-location-updates", update.getDriverId(), event);
        } catch (Exception e) {
            log.error("Failed to publish location update to Kafka for driver {}", update.getDriverId(), e);
        }
    }
}
