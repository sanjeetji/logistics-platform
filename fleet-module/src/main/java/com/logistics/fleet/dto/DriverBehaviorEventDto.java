package com.logistics.fleet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverBehaviorEventDto {
    private String driverExternalId;
    private BehaviorEventType eventType;
    private LocalDateTime timestamp;
    private Double severity; // 0.0 to 1.0
    private Double latitude;
    private Double longitude;

    public enum BehaviorEventType {
        SPEEDING,
        HARD_BRAKING,
        HARSH_ACCELERATION,
        LONG_IDLING,
        PHONE_USAGE
    }
}
