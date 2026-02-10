package com.logistics.bff.mobile.controller;

import com.logistics.bff.mobile.dto.OptimizedOrderResponse;
import com.logistics.bff.mobile.service.MobilePayloadOptimizationService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bff/mobile")
@RequiredArgsConstructor
public class MobileOptimizedController {

    private final MobilePayloadOptimizationService optimizationService;

    /**
     * Optimized endpoint for mobile devices
     * - Minimal payload
     * - Default pagination
     * - Compressed data
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OptimizedOrderResponse>>> getOptimizedOrders(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<OptimizedOrderResponse> orders = optimizationService.getOptimizedOrders(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders fetched"));
    }
}
