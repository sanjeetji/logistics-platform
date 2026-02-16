package com.logistics.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDto {
    private String orderId;
    private Long driverId;
    private String eventType;
    private String message;
    private LocalDateTime timestamp;
}
