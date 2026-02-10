package com.logistics.routing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.service.RouteOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routing")
@RequiredArgsConstructor
public class RouteOptimizationController {

    private final RouteOptimizationService routeOptimizationService;

    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<RouteOptimizationResponse>> optimizeRoute(
            @RequestBody RouteOptimizationRequest request) {
        
        RouteOptimizationResponse response = routeOptimizationService.optimizeRoute(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Route optimized successfully"));
    }
}
