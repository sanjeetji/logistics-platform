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
public class OrchestrationEvent {
    private String eventId;
    private String traceId;
    private EventType type;
    private String sourceService;
    private Map<String, Object> details;
    private LocalDateTime timestamp;

    public enum EventType {
        SAGA_STARTED,
        STEP_COMPLETED,
        STEP_FAILED,
        SAGA_COMPLETED,
        SAGA_FAILED,
        COMPENSATION_TRIGGERED,
        ORDER_DISPATCH_INITIATED,
        ORDER_DISPATCHED,
        ORDER_DISPATCH_FAILED
    }
}
