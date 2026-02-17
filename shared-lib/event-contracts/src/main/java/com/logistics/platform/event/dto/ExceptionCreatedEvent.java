package com.logistics.platform.event.dto;

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
public class ExceptionCreatedEvent implements Serializable {
    private Long id;
    private String orderId;
    private Long driverId;
    private String type;
    private String severity;
    private String description;
    private LocalDateTime timestamp;
}
