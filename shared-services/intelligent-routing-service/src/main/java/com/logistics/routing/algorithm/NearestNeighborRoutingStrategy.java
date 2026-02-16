package com.logistics.routing.algorithm;

import com.logistics.routing.dto.LocationDTO;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class NearestNeighborRoutingStrategy implements RoutingAlgorithm {

    @Override
    public RouteOptimizationRequest.OptimizationType getType() {
        return RouteOptimizationRequest.OptimizationType.TSP_NEAREST_NEIGHBOR;
    }

    @Override
    public RouteOptimizationResponse optimize(RouteOptimizationRequest request) {
        log.info("Executing Nearest Neighbor TSP optimization");

        List<LocationDTO> waypoints = request.getWaypoints();
        if (waypoints == null || waypoints.isEmpty()) {
            return RouteOptimizationResponse.builder()
                    .routes(Collections.emptyList())
                    .build();
        }

        LocationDTO start = request.getStartLocation();
        if (start == null && !waypoints.isEmpty()) {
            start = waypoints.get(0);
        }

        // Run NN Algorithm
        List<LocationDTO> optimized = nearestNeighborTSP(start, waypoints);

        // Return to start if requested
        if (Boolean.TRUE.equals(request.getReturnToStart()) && start != null) {
            optimized.add(start);
        }

        // Calculate total distance
        double totalDist = calculateTotalDistance(optimized);

        RouteOptimizationResponse.OptimizedRouteDTO routeDTO = RouteOptimizationResponse.OptimizedRouteDTO.builder()
                .waypoints(optimized)
                .distanceKm(totalDist)
                .vehicleId(request.getVehicleIds() != null && !request.getVehicleIds().isEmpty()
                        ? request.getVehicleIds().get(0)
                        : null)
                .build();

        return RouteOptimizationResponse.builder()
                .routes(List.of(routeDTO))
                .totalDistanceKm(totalDist)
                .algorithm("NEAREST_NEIGHBOR")
                .build();
    }

    private List<LocationDTO> nearestNeighborTSP(LocationDTO start, List<LocationDTO> waypoints) {
        List<LocationDTO> unvisited = new ArrayList<>(waypoints);
        List<LocationDTO> route = new ArrayList<>();

        // If start is separate from waypoints, add it first.
        // If start is implicitly the first waypoint, handle accordingly.
        // Here assuming start is the depot.
        route.add(start);
        if (unvisited.contains(start)) {
            unvisited.remove(start);
        }

        LocationDTO current = start;

        while (!unvisited.isEmpty()) {
            LocationDTO nearest = findNearestLocation(current, unvisited);
            route.add(nearest);
            unvisited.remove(nearest);
            current = nearest;
        }

        return route;
    }

    private LocationDTO findNearestLocation(LocationDTO from, List<LocationDTO> candidates) {
        LocationDTO nearest = candidates.get(0);
        double minDistance = calculateDistance(from, nearest);

        for (LocationDTO candidate : candidates) {
            double distance = calculateDistance(from, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = candidate;
            }
        }

        return nearest;
    }

    private double calculateDistance(LocationDTO loc1, LocationDTO loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadiusKm = 6371;

        return earthRadiusKm * c;
    }

    private double calculateTotalDistance(List<LocationDTO> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += calculateDistance(route.get(i), route.get(i + 1));
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
