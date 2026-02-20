package com.logistics.routing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.analysis.WhatIfAnalysisRequest;
import com.logistics.routing.analysis.WhatIfAnalysisResponse;
import com.logistics.routing.analysis.WhatIfAnalysisService;
import com.logistics.routing.simulation.RouteSimulationRequest;
import com.logistics.routing.simulation.RouteSimulationResponse;
import com.logistics.routing.simulation.RouteSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Advanced Features Controller
 */
@RestController
@RequestMapping("/api/v1/routes/advanced")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Advanced Features", description = "What-if analysis and route simulation APIs")
public class AdvancedFeaturesController {

    private final WhatIfAnalysisService whatIfAnalysisService;
    private final RouteSimulationService routeSimulationService;

    /**
     * Perform what-if analysis
     */
    @PostMapping("/what-if")
    @Operation(summary = "What-if analysis", description = "Analyze impact of route changes before applying")
    public ResponseEntity<ApiResponse<WhatIfAnalysisResponse>> analyzeWhatIf(
            @RequestBody WhatIfAnalysisRequest request) {
        
        log.info("What-if analysis request: scenario={}", request.getScenarioType());
        
        WhatIfAnalysisResponse response = whatIfAnalysisService.analyzeScenario(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Simulate route execution
     */
    @PostMapping("/simulate")
    @Operation(summary = "Simulate route", description = "Simulate route execution for validation")
    public ResponseEntity<ApiResponse<RouteSimulationResponse>> simulateRoute(
            @RequestBody RouteSimulationRequest request) {
        
        log.info("Route simulation request: route={}", request.getRouteId());
        
        RouteSimulationResponse response = routeSimulationService.simulateRoute(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
