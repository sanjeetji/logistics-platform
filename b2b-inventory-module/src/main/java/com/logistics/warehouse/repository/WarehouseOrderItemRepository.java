package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.WarehouseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseOrderItemRepository extends JpaRepository<WarehouseOrderItem, Long> {
    List<WarehouseOrderItem> findByWarehouseOrderId(Long warehouseOrderId);
}
