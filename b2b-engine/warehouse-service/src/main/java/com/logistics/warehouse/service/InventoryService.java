package com.logistics.warehouse.service;

import com.logistics.warehouse.model.InventoryItem;
import com.logistics.warehouse.model.InventoryTransaction;
import com.logistics.warehouse.model.TransactionType;
import com.logistics.warehouse.repository.InventoryItemRepository;
import com.logistics.warehouse.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for inventory management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository transactionRepository;

    /**
     * Add stock (IN transaction)
     */
    @Transactional
    public InventoryItem addStock(Long warehouseId, String sku, Integer quantity, String reason, String performedBy) {
        log.info("Adding {} units of {} to warehouse {}", quantity, sku, warehouseId);

        InventoryItem item = inventoryItemRepository.findByWarehouseIdAndSku(warehouseId, sku)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        Integer quantityBefore = item.getQuantity() != null ? item.getQuantity() : 0;
        item.setQuantity(quantityBefore + quantity);
        item.setLastRestocked(LocalDateTime.now());

        // Record transaction
        recordTransaction(warehouseId, item.getId(), TransactionType.IN, quantity,
                quantityBefore, item.getQuantity(), null, reason, performedBy);

        return inventoryItemRepository.save(item);
    }

    /**
     * Remove stock (OUT transaction)
     */
    @Transactional
    public InventoryItem removeStock(Long warehouseId, String sku, Integer quantity,
            String orderId, String reason, String performedBy) {
        log.info("Removing {} units of {} from warehouse {}", quantity, sku, warehouseId);

        InventoryItem item = inventoryItemRepository.findByWarehouseIdAndSku(warehouseId, sku)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        if (item.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + item.getAvailableQuantity());
        }

        Integer quantityBefore = item.getQuantity() != null ? item.getQuantity() : 0;
        item.setQuantity(quantityBefore - quantity);

        // Record transaction
        recordTransaction(warehouseId, item.getId(), TransactionType.OUT, quantity,
                quantityBefore, item.getQuantity(), orderId, reason, performedBy);

        return inventoryItemRepository.save(item);
    }

    /**
     * Reserve stock for order
     */
    @Transactional
    public InventoryItem reserveStock(Long warehouseId, String sku, Integer quantity, String orderId) {
        log.info("Reserving {} units of {} for order {}", quantity, sku, orderId);

        InventoryItem item = inventoryItemRepository.findByWarehouseIdAndSku(warehouseId, sku)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        if (item.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for reservation");
        }

        Integer quantityBefore = item.getReservedQuantity();
        item.setReservedQuantity(item.getReservedQuantity() + quantity);

        // Record transaction
        recordTransaction(warehouseId, item.getId(), TransactionType.RESERVE, quantity,
                quantityBefore, item.getReservedQuantity(), orderId, "Reserved for order", "SYSTEM");

        return inventoryItemRepository.save(item);
    }

    /**
     * Release reserved stock
     */
    @Transactional
    public InventoryItem releaseReservation(Long warehouseId, String sku, Integer quantity, String orderId) {
        log.info("Releasing {} units of {} for order {}", quantity, sku, orderId);

        InventoryItem item = inventoryItemRepository.findByWarehouseIdAndSku(warehouseId, sku)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));

        Integer quantityBefore = item.getReservedQuantity();
        item.setReservedQuantity(Math.max(0, item.getReservedQuantity() - quantity));

        // Record transaction
        recordTransaction(warehouseId, item.getId(), TransactionType.RELEASE, quantity,
                quantityBefore, item.getReservedQuantity(), orderId, "Reservation released", "SYSTEM");

        return inventoryItemRepository.save(item);
    }

    /**
     * Get low stock items
     */
    public List<InventoryItem> getLowStockItems(Long warehouseId) {
        if (warehouseId != null) {
            return inventoryItemRepository.findLowStockItems(warehouseId);
        }
        return inventoryItemRepository.findAllLowStockItems();
    }

    /**
     * Get warehouse inventory
     */
    public List<InventoryItem> getWarehouseInventory(Long warehouseId) {
        return inventoryItemRepository.findByWarehouseId(warehouseId);
    }

    /**
     * Record inventory transaction
     */
    private void recordTransaction(Long warehouseId, Long itemId, TransactionType type,
            Integer quantity, Integer quantityBefore, Integer quantityAfter,
            String orderId, String reason, String performedBy) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .warehouseId(warehouseId)
                .itemId(itemId)
                .transactionType(type)
                .quantity(quantity)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .orderId(orderId)
                .reason(reason)
                .performedBy(performedBy)
                .build();

        if (transaction == null) {
            throw new RuntimeException("Transaction cannot be null");
        }
        transactionRepository.save(transaction);
    }
}
