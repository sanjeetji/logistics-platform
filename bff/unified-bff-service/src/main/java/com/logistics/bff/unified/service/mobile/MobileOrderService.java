package com.logistics.bff.unified.service.mobile;

import com.logistics.bff.unified.client.order.OrderServiceClient;
import com.logistics.platform.dto.order.OrderDTO;
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
        log.info("Driver accepting order: {}", orderId);
        try {
            return orderClient.updateOrderStatus(orderId, "CONFIRMED");
        } catch (Exception e) {
            log.error("Failed to accept order", e);
            throw new RuntimeException("Failed to accept order: " + e.getMessage());
        }
    }

    /**
     * Driver rejects an order
     */
    public OrderDTO rejectOrder(String orderId) {
        log.info("Driver rejecting order: {}", orderId);
        try {
            return orderClient.updateOrderStatus(orderId, "REJECTED");
        } catch (Exception e) {
            log.error("Failed to reject order", e);
            throw new RuntimeException("Failed to reject order: " + e.getMessage());
        }
    }
}
