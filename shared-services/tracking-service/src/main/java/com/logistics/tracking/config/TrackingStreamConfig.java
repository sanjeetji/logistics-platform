package com.logistics.tracking.config;

import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TrackingStreamConfig {

    @Bean
    public java.util.function.Function<KStream<String, DriverLocationDto>, KStream<String, com.logistics.tracking.dto.TrackingEvent>> processDriverLocations() {
        return stream -> stream
                .mapValues(location -> {
                    log.info("Processing location for driver: {}", location.getDriverId());

                    // 1. Mock Fetch Active Order (In real life, join with KTable or lookup Redis)
                    // For now, assume every driver has an active order "ORD-123"
                    String orderId = "ORD-123";

                    // 2. Mock Calculate ETA
                    // Assume destination is (0,0) for simplicity or random logic
                    double distance = Math.sqrt(Math.pow(location.getLat(), 2) + Math.pow(location.getLng(), 2));
                    double etaSeconds = distance * 100; // Mock calculation

                    String eventType = "UPDATE";
                    String message = "On track";

                    // 3. Check for SLA Breach (Mock)
                    // If ETA > 30 minutes (1800s), predict breach
                    if (etaSeconds > 1800) {
                        eventType = "SLA_BREACH_PREDICTED";
                        message = "High probability of delay";
                        log.warn("SLA Breach Predicted for Driver {}", location.getDriverId());
                    }

                    return com.logistics.tracking.dto.TrackingEvent.builder()
                            .orderId(orderId)
                            .driverId(Long.parseLong(location.getDriverId()))
                            .eventType(eventType)
                            .currentLat(location.getLat())
                            .currentLng(location.getLng())
                            .estimatedTimeRemainingSeconds(etaSeconds)
                            .timestamp(java.time.LocalDateTime.now())
                            .message(message)
                            .build();
                });
    }
}
