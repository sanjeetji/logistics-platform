package com.logistics.order.repository;

import com.logistics.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderId(String orderId);

    // Optimized fetch for list views
    java.util.List<com.logistics.order.model.projection.OrderSummary> findByTenantId(String tenantId);

    java.util.List<com.logistics.order.model.projection.OrderSummary> findByDriverIdAndStatus(String driverId,
            com.logistics.order.model.OrderStatus status);
}
