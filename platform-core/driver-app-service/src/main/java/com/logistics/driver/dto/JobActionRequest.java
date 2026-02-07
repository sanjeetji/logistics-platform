package com.logistics.driver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobActionRequest {
    
    @NotNull(message = "Order ID is required")
    private String orderId;
    
    private String reason; // For rejection
}
