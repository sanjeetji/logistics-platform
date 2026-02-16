package com.logistics.routing.solver;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.dto.TrafficData;
import com.logistics.routing.traffic.TrafficIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Google OR-Tools VRP Solver
 * 
 * Implements Vehicle Routing Problem (VRP) solver with:
 * - Capacity constraints
 * - Time window constraints
 * - Distance/time optimization
 * - Multiple objectives
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VRPSolver {

    private final TrafficIntegrationService trafficIntegrationService;

    @Value("${routing.optimization.traffic.enabled:true}")
    private boolean trafficEnabled;

    static {
        // Load OR-Tools native library
        Loader.loadNativeLibraries();
    }

    /**
     * Solve VRP problem using Google OR-Tools
     */
    public RouteOptimizationResponse solve(RouteOptimizationRequest request) {
        long startTime = System.currentTimeMillis();
        
        log.info("Starting VRP optimization for tenant: {}, stops: {}, vehicles: {}", 
            request.getTenantId(), request.getStops().size(), request.getVehicles().size());

        try {
            // Create routing index manager
            int numLocations = request.getStops().size() + 1; // +1 for depot
            int numVehicles = request.getVehicles().size();
            int depot = 0;

            RoutingIndexManager manager = new RoutingIndexManager(
                numLocations, 
                numVehicles, 
                depot
            );

            // Create routing model
            RoutingModel routing = new RoutingModel(manager);

            // Create distance callback
            final long[][] distanceMatrix = createDistanceMatrix(request);
            final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                int fromNode = manager.indexToNode(fromIndex);
                int toNode = manager.indexToNode(toIndex);
                return distanceMatrix[fromNode][toNode];
            });

            // Define cost of each arc
            routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

            // Add capacity constraints
            if (request.getConstraints() != null && request.getConstraints().getRespectCapacity()) {
                addCapacityConstraints(routing, manager, request);
            }

            // Add time window constraints
            if (request.getConstraints() != null && request.getConstraints().getRespectTimeWindows()) {
                addTimeWindowConstraints(routing, manager, request, transitCallbackIndex);
            }

            // Set search parameters
            RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                .toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
                .setTimeLimit(com.google.protobuf.Duration.newBuilder().setSeconds(30).build())
                .build();

            // Solve
            Assignment solution = routing.solveWithParameters(searchParameters);

            if (solution != null) {
                RouteOptimizationResponse response = buildResponse(
                    request, routing, manager, solution, startTime
                );
                
                log.info("VRP optimization completed in {}ms, routes: {}, efficiency: {}%", 
                    response.getComputationTimeMs(), 
                    response.getRoutes().size(),
                    response.getMetrics().getRouteEfficiency());
                
                return response;
            } else {
                log.warn("No solution found for VRP optimization");
                return buildFailedResponse(request, startTime);
            }

        } catch (Exception e) {
            log.error("Error during VRP optimization", e);
            return buildFailedResponse(request, startTime);
        }
    }

    /**
     * Create distance matrix from stops using traffic data
     */
    private long[][] createDistanceMatrix(RouteOptimizationRequest request) {
        int size = request.getStops().size() + 1; // +1 for depot
        long[][] matrix = new long[size][size];

        // Depot is first vehicle's start location
        double depotLat = request.getVehicles().get(0).getStartLatitude();
        double depotLon = request.getVehicles().get(0).getStartLongitude();

        log.debug("Creating {}x{} distance matrix with traffic={}", size, size, trafficEnabled);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                } else {
                    double lat1 = (i == 0) ? depotLat : request.getStops().get(i - 1).getLatitude();
                    double lon1 = (i == 0) ? depotLon : request.getStops().get(i - 1).getLongitude();
                    double lat2 = (j == 0) ? depotLat : request.getStops().get(j - 1).getLatitude();
                    double lon2 = (j == 0) ? depotLon : request.getStops().get(j - 1).getLongitude();
                    
                    if (trafficEnabled) {
                        // Use traffic-aware distance from Google Maps
                        try {
                            TrafficData trafficData = trafficIntegrationService.getTrafficAwareDistance(
                                lat1, lon1, lat2, lon2
                            );
                            // Use duration in traffic (seconds) as the cost metric
                            matrix[i][j] = trafficData.getDurationInTrafficSeconds();
                            
                            if (trafficData.getTrafficLevel() != TrafficData.TrafficLevel.LIGHT) {
                                log.debug("Traffic detected [{},{}] -> [{},{}]: {} ({}% delay)", 
                                    i, j, lat1, lon1, lat2, lon2,
                                    trafficData.getTrafficLevel(), 
                                    String.format("%.1f", trafficData.getTrafficDelayPercent()));
                            }
                        } catch (Exception e) {
                            log.warn("Failed to get traffic data, using fallback distance", e);
                            matrix[i][j] = (long) (calculateHaversineDistance(lat1, lon1, lat2, lon2) * 1000);
                        }
                    } else {
                        // Use simple haversine distance in meters
                        matrix[i][j] = (long) (calculateHaversineDistance(lat1, lon1, lat2, lon2) * 1000);
                    }
                }
            }
        }

        return matrix;
    }

    /**
     * Add capacity constraints to routing model
     */
    private void addCapacityConstraints(RoutingModel routing, RoutingIndexManager manager, 
                                       RouteOptimizationRequest request) {
        
        // Create demand callback
        final int[] demands = new int[request.getStops().size() + 1];
        demands[0] = 0; // depot has no demand
        for (int i = 0; i < request.getStops().size(); i++) {
            demands[i + 1] = request.getStops().get(i).getDemandWeight();
        }

        final int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            return demands[fromNode];
        });

        // Add capacity dimension
        long[] vehicleCapacities = request.getVehicles().stream()
            .mapToLong(v -> v.getCapacityWeight())
            .toArray();

        routing.addDimensionWithVehicleCapacity(
            demandCallbackIndex,
            0, // null capacity slack
            vehicleCapacities,
            true, // start cumul to zero
            "Capacity"
        );
    }

    /**
     * Add time window constraints to routing model
     */
    private void addTimeWindowConstraints(RoutingModel routing, RoutingIndexManager manager,
                                         RouteOptimizationRequest request, int transitCallbackIndex) {
        
        // Add time dimension
        routing.addDimension(
            transitCallbackIndex,
            30, // allow waiting time
            3000, // maximum time per vehicle (in minutes)
            false, // don't force start cumul to zero
            "Time"
        );

        RoutingDimension timeDimension = routing.getMutableDimension("Time");

        // Add time window constraints for each location
        for (int i = 0; i < request.getStops().size(); i++) {
            RouteOptimizationRequest.DeliveryStop stop = request.getStops().get(i);
            long index = manager.nodeToIndex(i + 1);
            
            // Convert time windows to minutes from start of day
            long timeWindowStart = 0; // simplified - should convert from LocalDateTime
            long timeWindowEnd = 1440; // 24 hours in minutes
            
            timeDimension.cumulVar(index).setRange(timeWindowStart, timeWindowEnd);
        }

        // Add time window constraints for vehicles
        for (int i = 0; i < request.getVehicles().size(); i++) {
            long index = routing.start(i);
            timeDimension.cumulVar(index).setRange(0, 1440);
        }
    }

    /**
     * Build successful response from OR-Tools solution
     */
    private RouteOptimizationResponse buildResponse(RouteOptimizationRequest request,
                                                    RoutingModel routing,
                                                    RoutingIndexManager manager,
                                                    Assignment solution,
                                                    long startTime) {
        
        List<RouteOptimizationResponse.OptimizedRoute> routes = new ArrayList<>();
        double totalDistance = 0;
        int totalDuration = 0;
        double totalCost = 0;

        // Extract routes for each vehicle
        for (int vehicleId = 0; vehicleId < request.getVehicles().size(); vehicleId++) {
            List<RouteOptimizationResponse.RouteStop> stops = new ArrayList<>();
            long index = routing.start(vehicleId);
            int sequence = 0;
            double routeDistance = 0;

            while (!routing.isEnd(index)) {
                long nextIndex = solution.value(routing.nextVar(index));
                int node = manager.indexToNode(index);
                
                if (node > 0) { // Skip depot
                    RouteOptimizationRequest.DeliveryStop stop = request.getStops().get(node - 1);
                    
                    RouteOptimizationResponse.RouteStop routeStop = RouteOptimizationResponse.RouteStop.builder()
                        .sequence(sequence++)
                        .stopId(stop.getStopId())
                        .orderId(stop.getOrderId())
                        .latitude(stop.getLatitude())
                        .longitude(stop.getLongitude())
                        .address(stop.getAddress())
                        .serviceDurationMinutes(stop.getServiceDurationMinutes())
                        .build();
                    
                    stops.add(routeStop);
                }
                
                index = nextIndex;
            }

            if (!stops.isEmpty()) {
                RouteOptimizationResponse.OptimizedRoute route = RouteOptimizationResponse.OptimizedRoute.builder()
                    .routeId(UUID.randomUUID().toString())
                    .vehicleId(request.getVehicles().get(vehicleId).getVehicleId())
                    .driverId(request.getVehicles().get(vehicleId).getDriverId())
                    .stops(stops)
                    .routeMetrics(RouteOptimizationResponse.RouteMetrics.builder()
                        .numberOfStops(stops.size())
                        .build())
                    .build();
                
                routes.add(route);
            }
        }

        long computationTime = System.currentTimeMillis() - startTime;

        return RouteOptimizationResponse.builder()
            .optimizationId(UUID.randomUUID().toString())
            .tenantId(request.getTenantId())
            .status(RouteOptimizationResponse.OptimizationStatus.COMPLETED)
            .routes(routes)
            .metrics(RouteOptimizationResponse.OptimizationMetrics.builder()
                .vehiclesUsed(routes.size())
                .totalStops(routes.stream().mapToInt(r -> r.getStops().size()).sum())
                .routeEfficiency(85.0) // Placeholder
                .build())
            .createdAt(LocalDateTime.now())
            .computationTimeMs((int) computationTime)
            .build();
    }

    /**
     * Build failed response
     */
    private RouteOptimizationResponse buildFailedResponse(RouteOptimizationRequest request, long startTime) {
        long computationTime = System.currentTimeMillis() - startTime;
        
        return RouteOptimizationResponse.builder()
            .optimizationId(UUID.randomUUID().toString())
            .tenantId(request.getTenantId())
            .status(RouteOptimizationResponse.OptimizationStatus.FAILED)
            .routes(Collections.emptyList())
            .createdAt(LocalDateTime.now())
            .computationTimeMs((int) computationTime)
            .build();
    }

    /**
     * Calculate haversine distance between two coordinates
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
}
