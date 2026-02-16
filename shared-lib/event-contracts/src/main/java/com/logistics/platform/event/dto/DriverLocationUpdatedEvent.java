package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DriverLocationUpdatedEvent extends BaseEvent {
    private String driverId;
    private String orderId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
