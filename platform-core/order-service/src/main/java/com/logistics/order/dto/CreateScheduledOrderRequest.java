package com.logistics.order.dto;

import com.logistics.order.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateScheduledOrderRequest {

    @NotNull(message = "Order template is required")
    private Order orderTemplate;

    @NotNull(message = "Cron expression is required")
    private String cronExpression;

    @NotNull(message = "Customer ID is required")
    private String customerId;

    private String tenantId;
}
