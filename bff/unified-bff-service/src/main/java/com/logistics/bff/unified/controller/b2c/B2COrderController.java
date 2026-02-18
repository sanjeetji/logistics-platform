package com.logistics.bff.unified.controller.b2c;

import com.logistics.bff.unified.client.OrderServiceClient;
import com.logistics.bff.unified.service.B2COrderService;
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
 * B2C Order Controller
 * Handles order operations for B2C customers (Porter/BlueDart-style)
 */
@RestController
@RequestMapping("/api/v1/bff/b2c/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2C Orders", description = "Order management for B2C customers")
public class B2COrderController {

    private final OrderServiceClient orderClient;
    private final B2COrderService orderService;

    @GetMapping
    @Operation(summary = "Get customer orders", description = "Get all orders for a customer")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@RequestParam String customerId) {
        log.info("Fetching B2C orders for customer: {}", customerId);
        return ResponseEntity.ok(orderClient.getCustomerOrders(customerId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details", description = "Get detailed information about a specific order")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable String id) {
        log.info("Fetching B2C order: {}", id);
        return ResponseEntity.ok(orderClient.getOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Create order with pricing", description = "Create a new order with automatic pricing calculation")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Creating B2C order for customer: {}", request.getCustomerId());
        return ResponseEntity.ok(orderService.createOrderWithPricing(request));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an existing order")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable String id) {
        log.info("Cancelling B2C order: {}", id);
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
