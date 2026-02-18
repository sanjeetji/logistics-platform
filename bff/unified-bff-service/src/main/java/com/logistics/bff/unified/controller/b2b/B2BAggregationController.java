package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.ResponseAggregationService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/bff/b2b")
@RequiredArgsConstructor
public class B2BAggregationController {

    private final ResponseAggregationService aggregationService;

    /**
     * Single endpoint to fetch complete order details
     * Aggregates data from: order, tracking, driver, payment services
     */
    @GetMapping("/orders/{orderId}/complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompleteOrderDetails(
            @PathVariable String orderId) {
        
        Map<String, Object> aggregatedData = aggregationService.aggregateOrderDetails(orderId);
        return ResponseEntity.ok(ApiResponse.success(aggregatedData, "Order details aggregated"));
    }
}
