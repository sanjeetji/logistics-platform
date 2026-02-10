package com.logistics.bff.mobile.service;

import com.logistics.bff.mobile.client.OrderServiceClient;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.order.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mobile Order Service
 * Business logic for mobile order operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MobileOrderService {

    private final OrderServiceClient orderClient;

    /**
     * Driver accepts an order
     */
    public OrderDTO acceptOrder(String orderId) {
        try {
            return orderClient.updateOrderStatus(orderId,
                UpdateOrderRequest.builder()
                    .status("CONFIRMED")
                    .build());
        } catch (Exception e) {
            log.error("Failed to accept order: {}", orderId, e);
            throw new RuntimeException("Failed to accept order: " + e.getMessage());
        }
    }

    /**
     * Driver rejects an order
     */
    public OrderDTO rejectOrder(String orderId) {
        try {
            return orderClient.updateOrderStatus(orderId,
                UpdateOrderRequest.builder()
                    .status("REJECTED")
                    .build());
        } catch (Exception e) {
            log.error("Failed to reject order: {}", orderId, e);
            throw new RuntimeException("Failed to reject order: " + e.getMessage());
        }
    }
}
