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
}
