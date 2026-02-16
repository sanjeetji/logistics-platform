package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.WarehouseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseOrderRepository extends JpaRepository<WarehouseOrder, Long> {
    Optional<WarehouseOrder> findByOrderId(String orderId);

    List<WarehouseOrder> findByWarehouseIdAndStatus(Long warehouseId, WarehouseOrder.OrderStatus status);
}
