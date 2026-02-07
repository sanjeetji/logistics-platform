package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByWarehouseIdAndSku(Long warehouseId, String sku);

    List<InventoryItem> findByWarehouseId(Long warehouseId);

    List<InventoryItem> findBySku(String sku);

    @Query("SELECT i FROM InventoryItem i WHERE i.warehouseId = :warehouseId AND i.quantity <= i.reorderLevel")
    List<InventoryItem> findLowStockItems(Long warehouseId);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.reorderLevel")
    List<InventoryItem> findAllLowStockItems();
}
