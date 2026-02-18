package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.client.OrderServiceClient;
import com.logistics.platform.dto.order.CreateOrderRequest;
import com.logistics.platform.dto.order.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Mobile Customer Order Controller
 * Handles order operations for customer mobile app (B2C only)
 */
@RestController
@RequestMapping("/api/v1/mobile/customer/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Orders", description = "Order management for customer mobile app")
public class CustomerOrderController {

    private final OrderServiceClient orderClient;

    @GetMapping
    @Operation(summary = "Get customer orders", description = "Get all orders for a customer")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@RequestParam String customerId) {
        log.info("Fetching mobile orders for customer: {}", customerId);
        return ResponseEntity.ok(orderClient.getCustomerOrders(customerId));
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order from mobile app")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Creating mobile order for customer: {}", request.getCustomerId());
        return ResponseEntity.ok(orderClient.createOrder(request));
    }
}
