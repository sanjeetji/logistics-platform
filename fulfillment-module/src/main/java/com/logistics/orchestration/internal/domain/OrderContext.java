package com.logistics.orchestration.internal.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public record OrderContext(
        String orderId,
        String customerId,
        BigDecimal amount,
        String pickupLocation,
        String deliveryLocation,
        String vehicleType,
        String assignedDriverId) implements Serializable {
}
