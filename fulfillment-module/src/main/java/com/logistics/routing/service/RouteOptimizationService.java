package com.logistics.routing.service;

import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.optimizer.MultiObjectiveOptimizer;
import com.logistics.routing.optimizer.RouteScore;
import com.logistics.routing.solver.VRPSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Route Optimization Service
 * 
 * Main service for route optimization operations
 */
@Service("routingRouteOptimizationService")
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {

    private final VRPSolver vrpSolver;
    private final MultiObjectiveOptimizer multiObjectiveOptimizer;

    /**
     * Optimize routes using VRP solver with multi-objective evaluation
     */
    public RouteOptimizationResponse optimize(RouteOptimizationRequest request) {
        log.info("Optimizing routes for tenant: {}, stops: {}, vehicles: {}",
                request.getTenantId(),
                request.getStops() != null ? request.getStops().size() : 0,
                request.getVehicles() != null ? request.getVehicles().size() : 0);

        // Validate request
        validateRequest(request);

        // Solve VRP
        RouteOptimizationResponse response = vrpSolver.solve(request);

        // Evaluate routes with multi-objective scoring
        if (response.getRoutes() != null && !response.getRoutes().isEmpty()) {
            for (RouteOptimizationResponse.OptimizedRoute route : response.getRoutes()) {
                RouteScore score = multiObjectiveOptimizer.evaluateRoute(route, request);
                log.debug("Route {} scored: composite={}, cost={}, speed={}, green={}, driver={}",
                        route.getRouteId(),
                        String.format("%.1f", score.getCompositeScore()),
                        String.format("%.1f", score.getCostScore()),
                        String.format("%.1f", score.getSpeedScore()),
                        String.format("%.1f", score.getGreenScore()),
                        String.format("%.1f", score.getDriverSatisfactionScore()));
            }
        }

        log.info("Route optimization completed: status={}, routes={}, efficiency={}%",
                response.getStatus(),
                response.getRoutes().size(),
                response.getMetrics() != null ? response.getMetrics().getRouteEfficiency() : 0);

        return response;
    }

    /**
     * Validate optimization request
     */
    private void validateRequest(RouteOptimizationRequest request) {
        if (request.getStops() == null || request.getStops().isEmpty()) {
            throw new IllegalArgumentException("Stops cannot be empty");
        }

        if (request.getVehicles() == null || request.getVehicles().isEmpty()) {
            throw new IllegalArgumentException("Vehicles cannot be empty");
        }

        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        // Validate each stop
        for (RouteOptimizationRequest.DeliveryStop stop : request.getStops()) {
            if (stop.getLatitude() == null || stop.getLongitude() == null) {
                throw new IllegalArgumentException("Stop coordinates are required: " + stop.getStopId());
            }
        }

        // Validate each vehicle
        for (RouteOptimizationRequest.Vehicle vehicle : request.getVehicles()) {
            if (vehicle.getStartLatitude() == null || vehicle.getStartLongitude() == null) {
                throw new IllegalArgumentException("Vehicle start coordinates are required: " + vehicle.getVehicleId());
            }
        }
    }
}
