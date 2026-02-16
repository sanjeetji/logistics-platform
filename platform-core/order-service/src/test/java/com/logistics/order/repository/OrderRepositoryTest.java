package com.logistics.order.repository;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderType;
import com.logistics.order.model.projection.OrderSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldProjectOrderSummary() {
        // Given
        String tenantId = "tenant-123";
        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId("cust-1")
                .tenantId(tenantId)
                .status(OrderStatus.CREATED)
                .type(OrderType.B2C_ON_DEMAND)
                .metadata("some massive json blob")
                // Added required fields or defaults
                .price(java.math.BigDecimal.TEN)
                .weightKg(10.0)
                .pickupLocation(new com.logistics.order.model.OrderLocation())
                .dropLocation(new com.logistics.order.model.OrderLocation())
                .build();

        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // When
        List<OrderSummary> summaries = orderRepository.findByTenantId(tenantId);

        // Then
        assertThat(summaries).hasSize(1);
        OrderSummary summary = summaries.get(0);
        assertThat(summary.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(summary.getTenantId()).isEqualTo(tenantId);
        assertThat(summary.getStatus()).isEqualTo(OrderStatus.CREATED);

        // Ensure other fields are null or default if not in projection (though
        // interface projection usually just calls getter)
        // Main check is that it works
    }
}
