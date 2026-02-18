package com.logistics.bff.unified.controller;

import com.logistics.bff.unified.client.b2b.B2BOrderClient;
import com.logistics.bff.unified.client.order.OrderServiceClient;
import com.logistics.bff.unified.client.b2c.ParcelClient;
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
 * Unified Order Controller
 * Central entry point for all Order operations
 */
@RestController
@RequestMapping("/api/v1/bff/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Unified Orders", description = "Consolidated Order Management for all channels")
public class OrderController {

        private final OrderServiceClient orderClient;
        private final B2BOrderClient b2bOrderClient;
        private final ParcelClient parcelClient;

        @GetMapping("/{id}")
        @Operation(summary = "Get order details")
        public ResponseEntity<OrderDTO> getOrder(@PathVariable String id) {
                log.info("Fetching order details for: {}", id);
                return ResponseEntity.ok(orderClient.getOrderById(id));
        }

        @PostMapping
        @Operation(summary = "Create new order")
        public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO request) {
                log.info("Creating new unified order: {}", request.getType());
                return ResponseEntity.ok(orderClient.createOrder(request));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update order")
        public ResponseEntity<OrderDTO> updateOrder(@PathVariable String id, @RequestBody UpdateOrderRequest request) {
                log.info("Updating order: {}", id);
                return ResponseEntity.ok(orderClient.updateOrderStatus(id, request.getStatus()));
        }

        @GetMapping("/b2b")
        @Operation(summary = "List B2B orders")
        public ResponseEntity<List<OrderDTO>> getB2BOrders(
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String tenantId) {
                log.info("Fetching B2B orders for tenant: {}", tenantId);
                return ResponseEntity.ok(b2bOrderClient.getOrders(status, tenantId));
        }

        @GetMapping("/customer/{customerId}")
        @Operation(summary = "Get customer orders")
        public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable String customerId) {
                log.info("Fetching orders for customer: {}", customerId);
                return ResponseEntity.ok(orderClient.getOrders(null, null, customerId));
        }

        @GetMapping("/driver/{driverId}")
        @Operation(summary = "Get driver orders")
        public ResponseEntity<List<OrderDTO>> getDriverOrders(
                        @PathVariable String driverId,
                        @RequestParam(required = false) String status) {
                log.info("Fetching orders for driver: {}", driverId);
                return ResponseEntity.ok(orderClient.getOrders(status, driverId, null));
        }
}
