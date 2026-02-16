package com.logistics.routing.analysis;

import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.optimizer.MultiObjectiveOptimizer;
import com.logistics.routing.optimizer.RouteScore;
import com.logistics.routing.solver.VRPSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * What-If Analysis Service
 * 
 * Allows testing different scenarios before applying changes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WhatIfAnalysisService {

    private final VRPSolver vrpSolver;
    private final MultiObjectiveOptimizer multiObjectiveOptimizer;

    /**
     * Analyze what-if scenario
     */
    public WhatIfAnalysisResponse analyzeScenario(WhatIfAnalysisRequest request) {
        long startTime = System.currentTimeMillis();
        
        log.info("Analyzing what-if scenario: type={}, description={}", 
            request.getScenarioType(), request.getDescription());

        try {
            // Get baseline route (in real implementation, fetch from database)
            RouteOptimizationRequest baselineRequest = buildBaselineRequest(request);
            RouteOptimizationResponse baselineResponse = vrpSolver.solve(baselineRequest);
            RouteScore baselineScore = evaluateRoute(baselineResponse, baselineRequest);
            
            // Build scenario request
            RouteOptimizationRequest scenarioRequest = buildScenarioRequest(request, baselineRequest);
            RouteOptimizationResponse scenarioResponse = vrpSolver.solve(scenarioRequest);
            RouteScore scenarioScore = evaluateRoute(scenarioResponse, scenarioRequest);
            
            // Calculate deltas
            double costDelta = scenarioScore.getTotalCost() - baselineScore.getTotalCost();
            double durationDelta = scenarioScore.getTotalDurationHours() - baselineScore.getTotalDurationHours();
            double co2Delta = scenarioScore.getTotalCO2Kg() - baselineScore.getTotalCO2Kg();
            
            // Generate recommendation
            String recommendation = generateRecommendation(costDelta, durationDelta, co2Delta);
            boolean recommendApply = shouldApplyScenario(costDelta, durationDelta, co2Delta);
            
            WhatIfAnalysisResponse response = WhatIfAnalysisResponse.builder()
                .analysisId(UUID.randomUUID().toString())
                .scenarioDescription(request.getDescription())
                .baselineScore(baselineScore)
                .baselineCost(baselineScore.getTotalCost())
                .baselineDuration(baselineScore.getTotalDurationHours())
                .baselineCO2(baselineScore.getTotalCO2Kg())
                .scenarioScore(scenarioScore)
                .scenarioCost(scenarioScore.getTotalCost())
                .scenarioDuration(scenarioScore.getTotalDurationHours())
                .scenarioCO2(scenarioScore.getTotalCO2Kg())
                .costDelta(costDelta)
                .costDeltaPercent((costDelta / baselineScore.getTotalCost()) * 100)
                .durationDelta(durationDelta)
                .durationDeltaPercent((durationDelta / baselineScore.getTotalDurationHours()) * 100)
                .co2Delta(co2Delta)
                .co2DeltaPercent((co2Delta / baselineScore.getTotalCO2Kg()) * 100)
                .recommendation(recommendation)
                .recommendApply(recommendApply)
                .computationTimeMs(System.currentTimeMillis() - startTime)
                .build();
            
            log.info("What-if analysis completed: cost delta={}, duration delta={}, CO2 delta={}", 
                String.format("%.2f", costDelta),
                String.format("%.2f", durationDelta),
                String.format("%.2f", co2Delta));
            
            return response;
            
        } catch (Exception e) {
            log.error("Error during what-if analysis", e);
            throw e;
        }
    }

    private RouteOptimizationRequest buildBaselineRequest(WhatIfAnalysisRequest request) {
        // In real implementation, fetch from database
        return new RouteOptimizationRequest();
    }

    private RouteOptimizationRequest buildScenarioRequest(WhatIfAnalysisRequest request, 
                                                         RouteOptimizationRequest baseline) {
        // Clone baseline and apply scenario changes
        RouteOptimizationRequest scenario = new RouteOptimizationRequest();
        // Apply scenario modifications based on type
        return scenario;
    }

    private RouteScore evaluateRoute(RouteOptimizationResponse response, RouteOptimizationRequest request) {
        if (response.getRoutes() != null && !response.getRoutes().isEmpty()) {
            return multiObjectiveOptimizer.evaluateRoute(response.getRoutes().get(0), request);
        }
        return new RouteScore();
    }

    private String generateRecommendation(double costDelta, double durationDelta, double co2Delta) {
        if (costDelta < 0 && durationDelta < 0 && co2Delta < 0) {
            return "Scenario improves all metrics. Strongly recommended.";
        } else if (costDelta > 0 && durationDelta > 0 && co2Delta > 0) {
            return "Scenario worsens all metrics. Not recommended.";
        } else {
            return String.format("Mixed impact: Cost %s%.2f, Duration %s%.2f hrs, CO2 %s%.2f kg",
                costDelta >= 0 ? "+" : "", costDelta,
                durationDelta >= 0 ? "+" : "", durationDelta,
                co2Delta >= 0 ? "+" : "", co2Delta);
        }
    }

    private boolean shouldApplyScenario(double costDelta, double durationDelta, double co2Delta) {
        // Simple heuristic: apply if at least 2 out of 3 metrics improve
        int improvements = 0;
        if (costDelta < 0) improvements++;
        if (durationDelta < 0) improvements++;
        if (co2Delta < 0) improvements++;
        return improvements >= 2;
    }
}
