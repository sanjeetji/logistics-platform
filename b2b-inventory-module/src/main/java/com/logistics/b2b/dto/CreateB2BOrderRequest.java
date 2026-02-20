package com.logistics.b2b.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateB2BOrderRequest {
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    private String orderType; // SINGLE, MULTI_STOP, RECURRING
    
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    
    @NotNull(message = "SLA deadline is required")
    private LocalDateTime slaDeadline;
    
    private LocalDateTime scheduledPickupTime;
    
    private LocalDateTime scheduledDeliveryTime;
    
    private List<OrderStopDTO> stops;
    
    private Map<String, Object> metadata;
    
    private String notes;
}
