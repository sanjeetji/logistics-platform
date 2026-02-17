package com.logistics.order.controller;

import com.logistics.order.dto.AssignDriverRequest;
import com.logistics.order.dto.CancelOrderRequest;
import com.logistics.order.dto.OrderHistoryResponse;
import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStatusHistory;
import com.logistics.order.service.OrderHistoryService;
import com.logistics.order.service.OrderService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
        private final OrderService orderService;
        private final OrderHistoryService historyService;

        @GetMapping
        public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
                return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders()));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
                return orderService.getOrderById(id)
                                .map(order -> ResponseEntity.ok(ApiResponse.success(order)))
                                .orElse(ResponseEntity.ok(ApiResponse.error("Order not found")));
        }

        @GetMapping("/order/{orderId}")
        public ResponseEntity<ApiResponse<Order>> getOrderByOrderId(@PathVariable String orderId) {
                return orderService.getOrderByOrderId(orderId)
                                .map(order -> ResponseEntity.ok(ApiResponse.success(order)))
                                .orElse(ResponseEntity.ok(ApiResponse.error("Order not found")));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.createOrder(order),
                                "Order created successfully"));
        }

        @PatchMapping("/{id}/status")
        public ResponseEntity<ApiResponse<Order>> updateStatus(
                        @PathVariable Long id,
                        @RequestParam OrderStatus status) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.updateStatus(id, status),
                                "Order status updated to " + status));
        }

        @PostMapping("/{orderId}/assign")
        public ResponseEntity<ApiResponse<Order>> assignDriver(
                        @PathVariable String orderId,
                        @Valid @RequestBody AssignDriverRequest request) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.assignDriver(orderId, request.getDriverId(), request.getVehicleId()),
                                "Driver assigned successfully"));
        }

        @PostMapping("/{orderId}/cancel")
        public ResponseEntity<ApiResponse<Order>> cancelOrder(
                        @PathVariable String orderId,
                        @Valid @RequestBody CancelOrderRequest request) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.cancelOrder(orderId, request.getReason()),
                                "Order cancelled successfully"));
        }

        @PostMapping("/{orderId}/pickup")
        public ResponseEntity<ApiResponse<Order>> markPickedUp(@PathVariable String orderId) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.markPickedUp(orderId),
                                "Order marked as picked up"));
        }

        @PostMapping("/{orderId}/transit")
        public ResponseEntity<ApiResponse<Order>> markInTransit(@PathVariable String orderId) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.markInTransit(orderId),
                                "Order marked as in transit"));
        }

        @PostMapping("/{orderId}/deliver")
        public ResponseEntity<ApiResponse<Order>> markDelivered(
                        @PathVariable String orderId,
                        @RequestBody(required = false) com.logistics.order.dto.MarkDeliveredRequest request) {
                String photoUrl = request != null ? request.getPhotoUrl() : null;
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.markDelivered(orderId, photoUrl),
                                "Order marked as delivered"));
        }

        @PatchMapping("/{orderId}/preferences")
        public ResponseEntity<ApiResponse<Order>> updateDeliveryPreferences(
                        @PathVariable String orderId,
                        @Valid @RequestBody com.logistics.order.dto.DeliveryPreferencesRequest request) {
                return ResponseEntity.ok(ApiResponse.success(
                                orderService.updateDeliveryPreferences(orderId, request),
                                "Delivery preferences updated successfully"));
        }

        @GetMapping("/{orderId}/history")
        public ResponseEntity<ApiResponse<List<OrderHistoryResponse>>> getOrderHistory(@PathVariable String orderId) {
                List<OrderStatusHistory> history = historyService.getOrderHistory(orderId);
                List<OrderHistoryResponse> response = history.stream()
                                .map(h -> OrderHistoryResponse.builder()
                                                .id(h.getId())
                                                .previousStatus(h.getPreviousStatus())
                                                .newStatus(h.getNewStatus())
                                                .changedAt(h.getChangedAt())
                                                .changedBy(h.getChangedBy())
                                                .reason(h.getReason())
                                                .latitude(h.getLatitude())
                                                .longitude(h.getLongitude())
                                                .notes(h.getNotes())
                                                .build())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(ApiResponse.success(response));
        }

        @GetMapping("/completed")
        public ResponseEntity<ApiResponse<List<Order>>> getCompletedOrders(
                        @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime start,
                        @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime end) {
                return ResponseEntity.ok(ApiResponse.success(orderService.getCompletedOrdersForPeriod(start, end)));
        }

        @GetMapping("/demand")
        public ResponseEntity<ApiResponse<Integer>> getDemand() {
                return ResponseEntity.ok(ApiResponse.success(orderService.getDemand()));
        }
}
