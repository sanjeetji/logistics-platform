package com.logistics.routing.controller;

import com.logistics.platform.common.dto.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.service.RouteOptimizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Route Optimization REST Controller
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationController {

    private final RouteOptimizationService routeOptimizationService;

    /**
     * Optimize routes for given stops and vehicles
     */
    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<RouteOptimizationResponse>> optimizeRoutes(
            @RequestBody RouteOptimizationRequest request) {
        
        log.info("Route optimization request received for tenant: {}", request.getTenantId());
        
        RouteOptimizationResponse response = routeOptimizationService.optimize(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Routes optimized successfully"));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Route Optimization Service is running"));
    }
}
