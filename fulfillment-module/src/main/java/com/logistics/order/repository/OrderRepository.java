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

        java.util.List<Order> findByStatusAndActualDeliveryTimeBetween(com.logistics.order.model.OrderStatus status,
                        java.time.LocalDateTime start, java.time.LocalDateTime end);

        @org.springframework.data.jpa.repository.Query("SELECT DISTINCT o FROM Order o JOIN o.stops s WHERE o.status IN (com.logistics.order.model.OrderStatus.ASSIGNED, com.logistics.order.model.OrderStatus.PICKED_UP, com.logistics.order.model.OrderStatus.IN_TRANSIT) AND s.completed = false AND (6371 * acos(cos(radians(:lat)) * cos(radians(s.location.latitude)) * cos(radians(s.location.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(s.location.latitude)))) < :radius")
        java.util.List<Order> findAffectedOrders(@org.springframework.data.repository.query.Param("lat") Double lat,
                        @org.springframework.data.repository.query.Param("lon") Double lon,
                        @org.springframework.data.repository.query.Param("radius") Double radius);
}
