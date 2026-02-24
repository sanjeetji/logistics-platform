package com.logistics.order.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Automated Order Merging Service
 * 
 * Periodically identifies and merges orders destined for the same location.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMergingService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * Periodically scan and merge eligible orders (every 5 minutes)
     */
    @Scheduled(fixedDelay = 300000)
    public void findAndMergeEligibleOrders() {
        log.info("Starting automated order merging scan...");

        // 1. Fetch pending orders
        List<Order> pendingOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.CREATED)
                .collect(Collectors.toList());

        if (pendingOrders.size() < 2) {
            return;
        }

        // 2. Group by Tenant, Customer, and Drop Address
        Map<String, List<Order>> groups = pendingOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getTenantId() + "|" +
                        o.getCustomerId() + "|" +
                        (o.getDropLocation() != null ? o.getDropLocation().getAddress() : "unknown")));

        // 3. Process each group
        for (Map.Entry<String, List<Order>> entry : groups.entrySet()) {
            List<Order> group = entry.getValue();
            if (group.size() >= 2) {
                log.info("Found {} mergeable orders for key: {}", group.size(), entry.getKey());

                List<String> orderIds = group.stream()
                        .map(Order::getOrderId)
                        .collect(Collectors.toList());

                try {
                    orderService.mergeOrders(orderIds);
                    log.info("Successfully merged group: {}", orderIds);
                } catch (Exception e) {
                    log.error("Failed to auto-merge orders {}: {}", orderIds, e.getMessage());
                }
            }
        }
    }
}
