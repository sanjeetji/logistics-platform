package com.logistics.bff.b2c.service;

import com.logistics.bff.b2c.client.OrderServiceClient;
import com.logistics.bff.b2c.client.PricingServiceClient;
import com.logistics.platform.dto.order.CreateOrderRequest;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.order.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public OrderDTO createOrderWithPricing(CreateOrderRequest request) {
        try {
            // Calculate price before creating order
            Double pricing = pricingClient.calculatePrice(
                request.getPickupAddress(),
                request.getDeliveryAddress(),
                request.getWeight()
            );
            
            // Set calculated amount (convert Double to BigDecimal)
            request.setAmount(java.math.BigDecimal.valueOf(pricing));
            
            // Create order
            return orderClient.createOrder(request);
        } catch (Exception e) {
            log.error("Failed to create order with pricing", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Cancel order
     */
    public OrderDTO cancelOrder(String orderId) {
        try {
            return orderClient.updateOrder(orderId, 
                UpdateOrderRequest.builder()
                    .status("CANCELLED")
                    .build());
        } catch (Exception e) {
            log.error("Failed to cancel order: {}", orderId, e);
            throw new RuntimeException("Failed to cancel order: " + e.getMessage());
        }
    }
}
