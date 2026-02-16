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
public class OrchestrationCommand {
    private String commandId;
    private String traceId;
    private CommandType type; // DISPATCH, ALLOCATE, ROUTE
    private String targetService; // dispatch-service, inventory-service
    private Map<String, Object> payload;
    private LocalDateTime timestamp;

    public enum CommandType {
        DISPATCH_ORDER,
        ALLOCATE_INVENTORY,
        CALCULATE_ROUTE,
        PROCESS_PAYMENT,
        CANCEL_ORDER
    }
}
