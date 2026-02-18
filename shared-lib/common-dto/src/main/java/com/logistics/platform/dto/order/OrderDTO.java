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
public class OrderDTO {
    private String orderId;
    private String trackingNumber;
    private String customerId;
    private String driverId;
    private String status; // PENDING, CONFIRMED, IN_TRANSIT, DELIVERED, CANCELLED
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime estimatedDelivery;
    private BigDecimal amount;
    private String paymentStatus; // PENDING, PAID, FAILED
    private String packageType;
    private Double weight;
    private String specialInstructions;
    private String tenantId;

    // Unified Order Fields
    private String type;
    private java.util.Map<String, Object> metadata;
    private java.util.List<OrderStopDto> stops;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
