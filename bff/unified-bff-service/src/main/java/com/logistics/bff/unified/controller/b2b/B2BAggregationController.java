package com.logistics.bff.unified.controller.b2b;

import com.logistics.bff.unified.service.b2b.ResponseAggregationService;
import com.logistics.platform.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B2B Aggregation Controller
 */
@RestController
@RequestMapping("/api/v1/bff/b2b")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "B2B Aggregation", description = "Aggregated data endpoints for B2B clients")
public class B2BAggregationController {

    private final ResponseAggregationService aggregationService;

    @GetMapping("/orders/{orderId}/complete")
    @Operation(summary = "Get complete order details")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompleteOrderDetails(@PathVariable String orderId) {
        log.info("B2B complete order details aggregation request: {}", orderId);
        Map<String, Object> data = aggregationService.aggregateOrderDetails(orderId);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .data(data)
                .build());
    }
}
