package com.logistics.customer.controller;

import com.logistics.customer.dto.CreateOrderRequest;
import com.logistics.customer.service.CustomerOrderService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    @PostMapping("/{customerId}/orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateOrderRequest request) {
        Map<String, Object> order = orderService.createOrder(customerId, request);
        return ResponseEntity.ok(ApiResponse.success(order, "Order created successfully"));
    }
}
