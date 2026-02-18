package com.logistics.bff.unified.controller.mobile;

import com.logistics.bff.unified.dto.mobile.OptimizedOrderResponse;
import com.logistics.bff.unified.service.mobile.MobilePayloadOptimizationService;
import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.dto.order.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Mobile Optimized Controller
 */
@RestController
@RequestMapping("/api/v1/bff/mobile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mobile Optimized", description = "Optimized endpoints for mobile")
public class MobileOptimizedController {

    private final MobilePayloadOptimizationService optimizationService;

    @GetMapping("/orders")
    @Operation(summary = "Get optimized orders")
    public ResponseEntity<ApiResponse<List<OptimizedOrderResponse>>> getOptimizedOrders(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Mobile optimized orders request for user: {}", userId);
        List<OptimizedOrderResponse> data = optimizationService.getOptimizedOrders(new ArrayList<>(), page, size);

        return ResponseEntity.ok(ApiResponse.<List<OptimizedOrderResponse>>builder()
                .success(true)
                .data(data)
                .build());
    }
}
