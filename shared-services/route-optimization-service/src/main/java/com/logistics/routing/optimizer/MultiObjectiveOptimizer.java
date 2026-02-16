package com.logistics.routing.optimizer;

import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Multi-Objective Route Optimizer
 * 
 * Evaluates routes based on multiple objectives:
 * - Cost (fuel, driver wages, vehicle maintenance)
 * - Speed (total duration, on-time delivery)
 * - Green (CO2 emissions, environmental impact)
 * - Driver Satisfaction (workload balance, break times)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MultiObjectiveOptimizer {

    @Value("${routing.optimization.objectives.cost-weight:0.4}")
    private double costWeight;

    @Value("${routing.optimization.objectives.speed-weight:0.3}")
    private double speedWeight;

    @Value("${routing.optimization.objectives.green-weight:0.2}")
    private double greenWeight;

    @Value("${routing.optimization.objectives.driver-satisfaction-weight:0.1}")
    private double driverSatisfactionWeight;

    // Cost factors
    private static final double FUEL_COST_PER_KM = 0.15; // USD per km
    private static final double DRIVER_WAGE_PER_HOUR = 25.0; // USD per hour
    private static final double VEHICLE_MAINTENANCE_PER_KM = 0.05; // USD per km

    // Green factors
    private static final double CO2_PER_KM = 0.27; // kg CO2 per km (diesel van)
    private static final double CO2_IDLE_PER_HOUR = 2.0; // kg CO2 per hour idling

    /**
     * Evaluate route with multi-objective scoring
     */
    public RouteScore evaluateRoute(RouteOptimizationResponse.OptimizedRoute route,
                                   RouteOptimizationRequest request) {
        
        log.debug("Evaluating route {} with multi-objective scoring", route.getRouteId());

        // Calculate raw metrics
        double totalCost = calculateTotalCost(route);
        double totalDurationHours = calculateTotalDuration(route);
        double totalCO2 = calculateCO2Emissions(route);
        double driverWorkload = calculateDriverWorkload(route);

        // Calculate individual objective scores (0-100, higher is better)
        double costScore = calculateCostScore(totalCost);
        double speedScore = calculateSpeedScore(totalDurationHours);
        double greenScore = calculateGreenScore(totalCO2);
        double driverSatisfactionScore = calculateDriverSatisfactionScore(driverWorkload);

        // Calculate weighted composite score
        OptimizationObjectives objectives = OptimizationObjectives.builder()
            .costWeight(costWeight)
            .speedWeight(speedWeight)
            .greenWeight(greenWeight)
            .driverSatisfactionWeight(driverSatisfactionWeight)
            .build();

        double compositeScore = calculateCompositeScore(
            costScore, speedScore, greenScore, driverSatisfactionScore, objectives
        );

        RouteScore score = RouteScore.builder()
            .costScore(costScore)
            .speedScore(speedScore)
            .greenScore(greenScore)
            .driverSatisfactionScore(driverSatisfactionScore)
            .compositeScore(compositeScore)
            .totalCost(totalCost)
            .totalDurationHours(totalDurationHours)
            .totalCO2Kg(totalCO2)
            .driverWorkloadScore(driverWorkload)
            .build();

        log.info("Route score: composite={}, cost={}, speed={}, green={}, driver={}", 
            String.format("%.1f", compositeScore),
            String.format("%.1f", costScore),
            String.format("%.1f", speedScore),
            String.format("%.1f", greenScore),
            String.format("%.1f", driverSatisfactionScore));

        return score;
    }

    /**
     * Calculate total cost (fuel + wages + maintenance)
     */
    private double calculateTotalCost(RouteOptimizationResponse.OptimizedRoute route) {
        double distanceKm = route.getRouteMetrics() != null && route.getRouteMetrics().getTotalDistanceKm() != null
            ? route.getRouteMetrics().getTotalDistanceKm()
            : 0.0;
        
        double durationHours = route.getRouteMetrics() != null && route.getRouteMetrics().getTotalDurationMinutes() != null
            ? route.getRouteMetrics().getTotalDurationMinutes() / 60.0
            : 0.0;

        double fuelCost = distanceKm * FUEL_COST_PER_KM;
        double wagesCost = durationHours * DRIVER_WAGE_PER_HOUR;
        double maintenanceCost = distanceKm * VEHICLE_MAINTENANCE_PER_KM;

        return fuelCost + wagesCost + maintenanceCost;
    }

    /**
     * Calculate total duration in hours
     */
    private double calculateTotalDuration(RouteOptimizationResponse.OptimizedRoute route) {
        if (route.getRouteMetrics() != null && route.getRouteMetrics().getTotalDurationMinutes() != null) {
            return route.getRouteMetrics().getTotalDurationMinutes() / 60.0;
        }
        return 0.0;
    }

    /**
     * Calculate CO2 emissions
     */
    private double calculateCO2Emissions(RouteOptimizationResponse.OptimizedRoute route) {
        double distanceKm = route.getRouteMetrics() != null && route.getRouteMetrics().getTotalDistanceKm() != null
            ? route.getRouteMetrics().getTotalDistanceKm()
            : 0.0;
        
        int numberOfStops = route.getStops() != null ? route.getStops().size() : 0;
        double idleTimeHours = numberOfStops * 0.1; // Assume 6 minutes per stop

        double drivingCO2 = distanceKm * CO2_PER_KM;
        double idleCO2 = idleTimeHours * CO2_IDLE_PER_HOUR;

        return drivingCO2 + idleCO2;
    }

    /**
     * Calculate driver workload (0-100, lower is better workload)
     */
    private double calculateDriverWorkload(RouteOptimizationResponse.OptimizedRoute route) {
        int numberOfStops = route.getStops() != null ? route.getStops().size() : 0;
        double durationHours = calculateTotalDuration(route);

        // Ideal: 8-10 stops, 6-8 hours
        double stopsPenalty = Math.abs(numberOfStops - 9) * 2.0;
        double durationPenalty = Math.abs(durationHours - 7) * 5.0;

        double workload = stopsPenalty + durationPenalty;
        return Math.max(0, 100 - workload);
    }

    /**
     * Calculate cost score (0-100, higher is better = lower cost)
     */
    private double calculateCostScore(double totalCost) {
        // Assume max acceptable cost is $500
        double maxCost = 500.0;
        double score = 100 * (1 - Math.min(totalCost / maxCost, 1.0));
        return Math.max(0, score);
    }

    /**
     * Calculate speed score (0-100, higher is better = faster)
     */
    private double calculateSpeedScore(double totalDurationHours) {
        // Assume max acceptable duration is 10 hours
        double maxDuration = 10.0;
        double score = 100 * (1 - Math.min(totalDurationHours / maxDuration, 1.0));
        return Math.max(0, score);
    }

    /**
     * Calculate green score (0-100, higher is better = lower emissions)
     */
    private double calculateGreenScore(double totalCO2Kg) {
        // Assume max acceptable CO2 is 100 kg
        double maxCO2 = 100.0;
        double score = 100 * (1 - Math.min(totalCO2Kg / maxCO2, 1.0));
        return Math.max(0, score);
    }

    /**
     * Calculate driver satisfaction score (already 0-100)
     */
    private double calculateDriverSatisfactionScore(double driverWorkload) {
        return driverWorkload;
    }

    /**
     * Calculate weighted composite score
     */
    private double calculateCompositeScore(double costScore, double speedScore, 
                                          double greenScore, double driverSatisfactionScore,
                                          OptimizationObjectives objectives) {
        
        return (costScore * objectives.getCostWeight()) +
               (speedScore * objectives.getSpeedWeight()) +
               (greenScore * objectives.getGreenWeight()) +
               (driverSatisfactionScore * objectives.getDriverSatisfactionWeight());
    }

    /**
     * Compare two routes and return the better one
     */
    public RouteOptimizationResponse.OptimizedRoute selectBetterRoute(
            RouteOptimizationResponse.OptimizedRoute route1,
            RouteOptimizationResponse.OptimizedRoute route2,
            RouteOptimizationRequest request) {
        
        RouteScore score1 = evaluateRoute(route1, request);
        RouteScore score2 = evaluateRoute(route2, request);

        log.info("Comparing routes: route1={}, route2={}", 
            score1.getCompositeScore(), score2.getCompositeScore());

        return score1.getCompositeScore() >= score2.getCompositeScore() ? route1 : route2;
    }

    /**
     * Rank routes by composite score
     */
    public List<RouteOptimizationResponse.OptimizedRoute> rankRoutes(
            List<RouteOptimizationResponse.OptimizedRoute> routes,
            RouteOptimizationRequest request) {
        
        return routes.stream()
            .sorted((r1, r2) -> {
                RouteScore score1 = evaluateRoute(r1, request);
                RouteScore score2 = evaluateRoute(r2, request);
                return Double.compare(score2.getCompositeScore(), score1.getCompositeScore());
            })
            .toList();
    }
}
