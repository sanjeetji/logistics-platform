package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.InventoryTransaction;
import com.logistics.warehouse.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByWarehouseId(Long warehouseId);

    List<InventoryTransaction> findByItemId(Long itemId);

    List<InventoryTransaction> findByOrderId(String orderId);

    List<InventoryTransaction> findByTransactionType(TransactionType type);

    List<InventoryTransaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
