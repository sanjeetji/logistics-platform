package com.logistics.order.controller;

import com.logistics.order.dto.CreateScheduledOrderRequest;
import com.logistics.order.model.ScheduledOrder;
import com.logistics.order.service.ScheduledOrderService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduled-orders")
@RequiredArgsConstructor
public class ScheduledOrderController {

    private final ScheduledOrderService scheduledOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduledOrder>> createScheduledOrder(
            @Valid @RequestBody CreateScheduledOrderRequest request) {
        ScheduledOrder scheduledOrder = scheduledOrderService.createScheduledOrder(
                request.getOrderTemplate(),
                request.getCronExpression(),
                request.getCustomerId(),
                request.getTenantId());
        return ResponseEntity.ok(ApiResponse.success(scheduledOrder, "Scheduled order created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduledOrder>>> getScheduledOrders(@RequestParam String customerId) {
        return ResponseEntity.ok(ApiResponse.success(scheduledOrderService.getScheduledOrders(customerId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteScheduledOrder(@PathVariable Long id) {
        scheduledOrderService.deleteScheduledOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Scheduled order cancelled successfully"));
    }
}
