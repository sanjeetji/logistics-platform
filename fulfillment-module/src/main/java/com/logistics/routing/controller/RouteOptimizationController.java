package com.logistics.routing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.solver.VRPSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/routing")
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationController {

    private final VRPSolver vrpSolver;
    private final com.logistics.routing.service.BatchOptimizationService batchOptimizationService;

    @PostMapping("/optimize")
    public ApiResponse<RouteOptimizationResponse> optimizeRoute(@RequestBody RouteOptimizationRequest request) {
        log.info("Received route optimization request for tenant: {}", request.getTenantId());

        try {
            RouteOptimizationResponse response = vrpSolver.solve(request);

            if (response.getStatus() == RouteOptimizationResponse.OptimizationStatus.COMPLETED) {
                return ApiResponse.success(response, "Route optimization completed successfully");
            } else {
                return ApiResponse.error("Failed to find optimal route solution");
            }
        } catch (Exception e) {
            log.error("Error during route optimization", e);
            return ApiResponse.error("Internal error during route optimization: " + e.getMessage());
        }
    }

    @PostMapping("/batch-optimize")
    public ApiResponse<RouteOptimizationResponse> batchOptimize(
            @org.springframework.web.bind.annotation.RequestParam("tenantId") String tenantId,
            @org.springframework.web.bind.annotation.RequestParam("lat") Double lat,
            @org.springframework.web.bind.annotation.RequestParam("lon") Double lon,
            @org.springframework.web.bind.annotation.RequestParam(value = "radius", defaultValue = "10000") Double radius) {

        log.info("Received batch optimization request for tenant: {} at ({}, {})", tenantId, lat, lon);

        try {
            RouteOptimizationResponse response = batchOptimizationService.optimizeBatch(tenantId, lat, lon, radius);

            if (response.getStatus() == RouteOptimizationResponse.OptimizationStatus.COMPLETED) {
                return ApiResponse.success(response, "Batch optimization completed and applied successfully");
            } else {
                return ApiResponse.error("Batch optimization failed or no pending orders: " + response.getMessage());
            }
        } catch (Exception e) {
            log.error("Error during batch optimization", e);
            return ApiResponse.error("Internal error during batch optimization: " + e.getMessage());
        }
    }
}
