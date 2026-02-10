package com.logistics.platform.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDTO {
    private String id;
    private String orderId;
    private String status;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private String createdBy; // SYSTEM, DRIVER, CUSTOMER
}
