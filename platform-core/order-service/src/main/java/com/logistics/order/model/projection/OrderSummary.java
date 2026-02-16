package com.logistics.order.model.projection;

import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Projection for list views to avoid fetching heavy fields like metadata or
 * full locations.
 */
public interface OrderSummary {
    String getOrderId();

    String getCustomerId();

    String getTenantId();

    OrderType getType();

    OrderStatus getStatus();

    BigDecimal getPrice();

    LocalDateTime getCreatedAt();
}
