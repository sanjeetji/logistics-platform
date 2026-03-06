package com.logistics.warehouse.service;

import com.logistics.platform.dto.warehouse.WarehouseOrderPackedEvent;
import com.logistics.warehouse.kafka.WarehouseOutboxService;
import com.logistics.warehouse.model.WarehouseOrder;
import com.logistics.warehouse.model.WarehouseOrderItem;
import com.logistics.warehouse.repository.WarehouseOrderItemRepository;
import com.logistics.warehouse.repository.WarehouseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseFulfillmentService {

    private final WarehouseOrderRepository orderRepository;
    private final WarehouseOrderItemRepository itemRepository;
    private final WarehouseInventoryService inventoryService;
    private final WarehouseOutboxService outboxService;

    /**
     * Start picking for an order
     */
    @Transactional
    public WarehouseOrder startPicking(String orderId) {
        log.info("Starting picking for order: {}", orderId);
        WarehouseOrder order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != WarehouseOrder.OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in PENDING state");
        }

        order.setStatus(WarehouseOrder.OrderStatus.PICKING);
        order.setPickingStartedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    /**
     * Pick an item for an order
     */
    @Transactional
    public void pickItem(String orderId, String sku, Integer quantity, Long binId) {
        log.info("Picking {} items of {} for order {}", quantity, sku, orderId);
        WarehouseOrder order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        List<WarehouseOrderItem> items = itemRepository.findByWarehouseOrderId(order.getId());
        WarehouseOrderItem item = items.stream()
                .filter(i -> i.getSku().equals(sku))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("SKU not found in order: " + sku));

        if (item.getPickedQuantity() + quantity > item.getQuantity()) {
            throw new RuntimeException("Picking more than ordered quantity");
        }

        item.setPickedQuantity(item.getPickedQuantity() + quantity);
        item.setPickingBinId(binId);
        itemRepository.save(item);
    }

    /**
     * Pack an order
     */
    @Transactional
    public WarehouseOrder packOrder(String orderId) {
        log.info("Packing order: {}", orderId);
        WarehouseOrder order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Verify all items picked
        List<WarehouseOrderItem> items = itemRepository.findByWarehouseOrderId(order.getId());
        for (WarehouseOrderItem item : items) {
            if (item.getPickedQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not all items picked for order: " + orderId);
            }
        }

        order.setStatus(WarehouseOrder.OrderStatus.PACKED);
        order.setPackingCompletedAt(LocalDateTime.now());
        WarehouseOrder savedOrder = orderRepository.save(order);

        // Emit Yard Management Event
        outboxService.publishOrderPackedEvent(WarehouseOrderPackedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .warehouseId(savedOrder.getWarehouseId())
                .packedAt(savedOrder.getPackingCompletedAt())
                .build());

        return savedOrder;
    }

    /**
     * Ship an order (Confirm fulfillment)
     */
    @Transactional
    public WarehouseOrder shipOrder(String orderId, String performedBy) {
        log.info("Shipping order: {}", orderId);
        WarehouseOrder order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != WarehouseOrder.OrderStatus.PACKED) {
            throw new RuntimeException("Order must be PACKED before shipping");
        }

        // Finalize inventory removal
        List<WarehouseOrderItem> items = itemRepository.findByWarehouseOrderId(order.getId());
        for (WarehouseOrderItem item : items) {
            inventoryService.removeStock(
                    order.getWarehouseId(),
                    item.getSku(),
                    item.getQuantity(),
                    orderId,
                    "Fulfillment complete",
                    performedBy);
        }

        order.setStatus(WarehouseOrder.OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
