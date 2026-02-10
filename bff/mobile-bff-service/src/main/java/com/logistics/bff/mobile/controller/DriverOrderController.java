package com.logistics.bff.mobile.controller;

import com.logistics.bff.mobile.client.OrderServiceClient;
import com.logistics.bff.mobile.service.MobileOrderService;
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
 * Mobile Driver Order Controller
 * Handles order operations for driver mobile app
 */
@RestController
@RequestMapping("/api/v1/mobile/driver/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Orders", description = "Order management for driver mobile app")
public class DriverOrderController {

    private final OrderServiceClient orderClient;
    private final MobileOrderService orderService;

    @GetMapping
    @Operation(summary = "Get assigned orders", description = "Get all orders assigned to driver")
    public ResponseEntity<List<OrderDTO>> getDriverOrders(
            @RequestParam String driverId,
            @RequestParam(required = false) String status) {
        log.info("Fetching orders for driver: {}, status: {}", driverId, status);
        return ResponseEntity.ok(orderClient.getDriverOrders(driverId, status));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Accept order", description = "Driver accepts an assigned order")
    public ResponseEntity<OrderDTO> acceptOrder(@PathVariable String id) {
        log.info("Driver accepting order: {}", id);
        return ResponseEntity.ok(orderService.acceptOrder(id));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject order", description = "Driver rejects an assigned order")
    public ResponseEntity<OrderDTO> rejectOrder(@PathVariable String id) {
        log.info("Driver rejecting order: {}", id);
        return ResponseEntity.ok(orderService.rejectOrder(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateOrderRequest request) {
        log.info("Updating order status: {} to {}", id, request.getStatus());
        return ResponseEntity.ok(orderClient.updateOrderStatus(id, request));
    }
}
