package com.logistics.locationhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateDTO implements Serializable {
    private String driverId;
    private String orderId;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double bearing;
    private Double speed;
    private Integer batteryLevel;
    private Boolean isMoving;
    private LocalDateTime timestamp;
}
