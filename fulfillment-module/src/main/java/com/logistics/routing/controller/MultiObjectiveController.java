package com.logistics.routing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.optimizer.MultiObjectiveOptimizer;
import com.logistics.routing.optimizer.OptimizationObjectives;
import com.logistics.routing.optimizer.RouteScore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Multi-Objective Optimization Controller
 */
@RestController
@RequestMapping("/api/v1/routes/optimize/multi-objective")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Multi-Objective Optimization", description = "Multi-objective route optimization APIs")
public class MultiObjectiveController {

    private final MultiObjectiveOptimizer multiObjectiveOptimizer;

    /**
     * Evaluate route with multi-objective scoring
     */
    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate route", description = "Evaluate a route with multi-objective scoring")
    public ResponseEntity<ApiResponse<RouteScore>> evaluateRoute(
            @RequestBody RouteOptimizationResponse.OptimizedRoute route,
            @RequestParam(required = false) Double costWeight,
            @RequestParam(required = false) Double speedWeight,
            @RequestParam(required = false) Double greenWeight,
            @RequestParam(required = false) Double driverSatisfactionWeight) {
        
        log.info("Evaluating route with multi-objective scoring: {}", route.getRouteId());
        
        // Create dummy request (in real implementation, fetch from context)
        RouteOptimizationRequest request = new RouteOptimizationRequest();
        
        RouteScore score = multiObjectiveOptimizer.evaluateRoute(route, request);
        
        return ResponseEntity.ok(ApiResponse.success(score));
    }

    /**
     * Get optimization objectives configuration
     */
    @GetMapping("/objectives")
    @Operation(summary = "Get objectives", description = "Get current optimization objectives configuration")
    public ResponseEntity<ApiResponse<OptimizationObjectives>> getObjectives() {
        
        // In real implementation, fetch from configuration
        OptimizationObjectives objectives = OptimizationObjectives.builder()
            .costWeight(0.4)
            .speedWeight(0.3)
            .greenWeight(0.2)
            .driverSatisfactionWeight(0.1)
            .build();
        
        return ResponseEntity.ok(ApiResponse.success(objectives));
    }

    /**
     * Update optimization objectives
     */
    @PutMapping("/objectives")
    @Operation(summary = "Update objectives", description = "Update optimization objectives weights")
    public ResponseEntity<ApiResponse<OptimizationObjectives>> updateObjectives(
            @RequestBody OptimizationObjectives objectives) {
        
        log.info("Updating optimization objectives: cost={}, speed={}, green={}, driver={}", 
            objectives.getCostWeight(),
            objectives.getSpeedWeight(),
            objectives.getGreenWeight(),
            objectives.getDriverSatisfactionWeight());
        
        // Validate and normalize
        if (!objectives.isValid()) {
            objectives.normalize();
            log.warn("Objectives normalized to sum to 1.0");
        }
        
        // In real implementation, save to configuration
        
        return ResponseEntity.ok(ApiResponse.success(objectives));
    }
}
