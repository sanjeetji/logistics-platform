package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Backorder Service
 * 
 * Manages orders that cannot be fulfilled due to inventory shortages.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BackorderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryService historyService;

    /**
     * Move an order to backorder status
     */
    @Transactional
    public void moveToBackorder(String orderId, String reason) {
        log.info("Moving order {} to backorder queue. Reason: {}", orderId, reason);

        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.BACKORDERED);
        orderRepository.save(order);

        historyService.recordStatusChange(orderId, previousStatus, OrderStatus.BACKORDERED,
                "SYSTEM", "Moved to backorder: " + reason, null, null);
    }

    /**
     * Reclaim orders containing a specific SKU when inventory is replenished.
     */
    @Transactional
    public void recoverOrdersBySku(String sku) {
        log.info("Attempting to recover backordered orders for SKU: {}", sku);

        List<Order> backorderedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.BACKORDERED)
                .filter(o -> o.getItems().stream().anyMatch(item -> sku.equals(item.getSku())))
                .collect(Collectors.toList());

        if (backorderedOrders.isEmpty()) {
            log.info("No backordered orders found for SKU: {}", sku);
            return;
        }

        log.info("Found {} backordered orders for SKU {}. Reclaiming...", backorderedOrders.size(), sku);

        for (Order order : backorderedOrders) {
            order.setStatus(OrderStatus.CREATED);
            orderRepository.save(order);
            historyService.recordStatusChange(order.getOrderId(), OrderStatus.BACKORDERED, OrderStatus.CREATED,
                    "SYSTEM", "Inventory replenished (SKU: " + sku + "). Order recovered.", null, null);
        }
    }

    /**
     * Periodically check backorder queue and attempt transition to CREATED or
     * direct re-routing
     * This would typically be triggered by an InventoryReplenishedEvent, but here
     * we add a poller for demonstration.
     */
    @Scheduled(fixedDelay = 60000) // Every minute
    @Transactional
    public void processBackorderQueue() {
        List<Order> backorderedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.BACKORDERED)
                .collect(Collectors.toList());

        if (backorderedOrders.isEmpty()) {
            return;
        }

        log.info("Processing {} backordered orders...", backorderedOrders.size());

        for (Order order : backorderedOrders) {
            // In a real system, we'd check with inventory-service here
            boolean isInventoryAvailable = checkInventory(order);

            if (isInventoryAvailable) {
                log.info("Inventory available for backordered order {}. Moving back to CREATED.", order.getOrderId());
                order.setStatus(OrderStatus.CREATED);
                orderRepository.save(order);

                historyService.recordStatusChange(order.getOrderId(), OrderStatus.BACKORDERED, OrderStatus.CREATED,
                        "SYSTEM", "Inventory replenished. Order moved back to pending.", null, null);

                // Trigger re-routing if needed (or wait for next batch optimization)
            }
        }
    }

    private boolean checkInventory(Order order) {
        // Mock logic: 50% chance stock is back for demo purposes
        return Math.random() > 0.5;
    }
}
