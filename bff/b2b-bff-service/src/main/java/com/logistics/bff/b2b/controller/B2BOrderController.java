package com.logistics.bff.b2b.controller;

import com.logistics.bff.b2b.client.OrderServiceClient;
import com.logistics.platform.dto.order.CreateOrderRequest;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.order.UpdateOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B2B Order Management Controller
 * Handles order operations for B2B clients (Bringg-style dashboard)
 */
@RestController
@RequestMapping("/api/v1/bff/b2b/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Orders", description = "Order management for B2B clients")
public class B2BOrderController {

    private final OrderServiceClient orderClient;

    @GetMapping
    @Operation(summary = "List all orders", description = "Get all orders with optional filters")
    public ResponseEntity<List<OrderDTO>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String tenantId) {
        log.info("Fetching B2B orders - status: {}, tenantId: {}", status, tenantId);
        return ResponseEntity.ok(orderClient.getOrders(status, null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details", description = "Get detailed information about a specific order")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable String id) {
        log.info("Fetching B2B order: {}", id);
        return ResponseEntity.ok(orderClient.getOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Create new order", description = "Create a new order for B2B client")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Creating B2B order for customer: {}", request.getCustomerId());
        return ResponseEntity.ok(orderClient.createOrder(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update an existing order")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable String id,
            @RequestBody UpdateOrderRequest request) {
        log.info("Updating B2B order: {}", id);
        return ResponseEntity.ok(orderClient.updateOrder(Long.parseLong(id), request));
    }

    @GetMapping("/count")
    @Operation(summary = "Get order count", description = "Get total count of orders by status")
    public ResponseEntity<Long> getOrderCount(@RequestParam(required = false) String status) {
        log.info("Fetching B2B order count - status: {}", status);
        return ResponseEntity.ok(orderClient.getOrderCount(status));
    }
}
