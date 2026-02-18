package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessExceptionEvent {
    private String exceptionId;
    private String serviceName;
    private String exceptionType;
    private String message;
    private String severity; // INFO, WARN, CRITICAL
    private LocalDateTime timestamp;
    private Map<String, String> metadata;
}
