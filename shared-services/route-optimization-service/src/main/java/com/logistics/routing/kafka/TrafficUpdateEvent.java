package com.logistics.routing.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Traffic Update Event from Kafka
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficUpdateEvent {

    private String eventId;
    private String eventType; // "INCIDENT", "CONGESTION", "ROAD_CLOSURE", "WEATHER"
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private String severity; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    private String description;
    private Long timestamp;
    private Long expiresAt;
}
