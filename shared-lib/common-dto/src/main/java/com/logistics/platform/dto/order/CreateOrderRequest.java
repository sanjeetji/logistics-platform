package com.logistics.platform.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String customerId;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime pickupTime;
    private String packageType;
    private Double weight;
    private String specialInstructions;
    private BigDecimal amount;
    private String paymentMethod; // CASH, CARD, WALLET, UPI
    private String tenantId;

    // Unified Order Fields
    private String type; // B2B_SHIPMENT, B2C_ON_DEMAND, etc.
    private java.util.Map<String, Object> metadata;
    private java.util.List<OrderStopDto> stops;
    private LocalDateTime scheduledTime;

}
