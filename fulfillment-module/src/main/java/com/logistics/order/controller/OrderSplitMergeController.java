package com.logistics.order.controller;

import com.logistics.order.model.Order;
import com.logistics.order.service.OrderService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Split and Merge operations on Orders
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderSplitMergeController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/split")
    public ApiResponse<Order> splitOrder(
            @PathVariable String orderId,
            @RequestBody SplitRequest request) {
        log.info("Received request to split order: {} move items: {}", orderId, request.getItemSkus());
        try {
            Order childOrder = orderService.splitOrder(orderId, request.getItemSkus());
            return ApiResponse.success(childOrder, "Order split successfully. New order: " + childOrder.getOrderId());
        } catch (Exception e) {
            log.error("Failed to split order: {}", e.getMessage());
            return ApiResponse.error("Failed to split order: " + e.getMessage());
        }
    }

    @PostMapping("/merge")
    public ApiResponse<Order> mergeOrders(@RequestBody MergeRequest request) {
        log.info("Received request to merge orders: {}", request.getOrderIds());
        try {
            Order masterOrder = orderService.mergeOrders(request.getOrderIds());
            return ApiResponse.success(masterOrder, "Orders merged successfully into: " + masterOrder.getOrderId());
        } catch (Exception e) {
            log.error("Failed to merge orders: {}", e.getMessage());
            return ApiResponse.error("Failed to merge orders: " + e.getMessage());
        }
    }

    @Data
    public static class SplitRequest {
        private List<String> itemSkus;
    }

    @Data
    public static class MergeRequest {
        private List<String> orderIds;
    }
}
