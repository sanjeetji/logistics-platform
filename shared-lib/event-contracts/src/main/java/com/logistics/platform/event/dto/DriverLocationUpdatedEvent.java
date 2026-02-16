package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverLocationUpdatedEvent extends BaseEvent {
    private String driverId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;

    public static DriverLocationUpdatedEvent create(String driverId, Double lat, Double lon) {
        return DriverLocationUpdatedEvent.builder()
                .driverId(driverId)
                .latitude(lat)
                .longitude(lon)
                .eventType("DRIVER_LOCATION_UPDATED")
                .build();
    }
}
