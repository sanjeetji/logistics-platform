package com.logistics.bff.unified.service.b2c;

import com.logistics.bff.unified.client.b2c.PricingServiceClient;
import com.logistics.bff.unified.client.order.OrderServiceClient;
import com.logistics.platform.dto.order.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * B2C Order Service
 * Business logic for B2C order operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2COrderService {

    private final OrderServiceClient orderClient;
    private final PricingServiceClient pricingClient;

    /**
     * Create order with automatic pricing calculation
     */
    public OrderDTO createOrderWithPricing(OrderDTO orderRequest) {
        log.info("Creating B2C order with pricing for customer: {}", orderRequest.getCustomerId());
        try {
            // 1. Calculate price
            Double calculatedPrice = pricingClient.calculatePrice(
                    orderRequest.getPickupAddress(),
                    orderRequest.getDeliveryAddress(),
                    orderRequest.getWeight() != null ? orderRequest.getWeight().doubleValue() : 0.0);

            // 2. Set price in order if successful
            if (calculatedPrice != null) {
                orderRequest.setAmount(BigDecimal.valueOf(calculatedPrice));
            }

            // 3. Create order
            return orderClient.createOrder(orderRequest);
        } catch (Exception e) {
            log.error("Failed to create B2C order with pricing", e);
            throw new RuntimeException("Order creation failed: " + e.getMessage());
        }
    }

    /**
     * Cancel order
     */
    public OrderDTO cancelOrder(String orderId) {
        log.info("Cancelling B2C order: {}", orderId);
        try {
            return orderClient.updateOrderStatus(orderId, "CANCELLED");
        } catch (Exception e) {
            log.error("Failed to cancel B2C order", e);
            throw new RuntimeException("Cancel failed: " + e.getMessage());
        }
    }
}
