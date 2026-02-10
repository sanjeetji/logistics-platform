package com.logistics.routing.service;

import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.model.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizationService {

    /**
     * Optimizes route using Nearest Neighbor algorithm (TSP approximation)
     * For production, integrate with Google OR-Tools or Jsprit for VRP solving
     */
    public RouteOptimizationResponse optimizeRoute(RouteOptimizationRequest request) {
        log.info("Optimizing route with {} waypoints", 
                request.getWaypoints() != null ? request.getWaypoints().size() : 0);

        List<Location> allStops = new ArrayList<>();
        allStops.add(request.getStartLocation());
        
        if (request.getWaypoints() != null && !request.getWaypoints().isEmpty()) {
            // Apply Nearest Neighbor algorithm
            List<Location> optimizedWaypoints = nearestNeighborTSP(
                    request.getStartLocation(), 
                    request.getWaypoints()
            );
            allStops.addAll(optimizedWaypoints);
        }
        
        if (request.getEndLocation() != null && !request.getReturnToStart()) {
            allStops.add(request.getEndLocation());
        } else if (request.getReturnToStart()) {
            allStops.add(request.getStartLocation());
        }

        // Calculate total distance and time
        double totalDistance = calculateTotalDistance(allStops);
        int estimatedTime = (int) (totalDistance * 2); // Rough estimate: 30 km/h average

        return RouteOptimizationResponse.builder()
                .optimizedRoute(allStops)
                .totalDistanceKm(totalDistance)
                .estimatedTimeMinutes(estimatedTime)
                .algorithm("NEAREST_NEIGHBOR")
                .savingsPercentage(calculateSavings(request, totalDistance))
                .build();
    }

    /**
     * Nearest Neighbor TSP approximation
     */
    private List<Location> nearestNeighborTSP(Location start, List<Location> waypoints) {
        List<Location> unvisited = new ArrayList<>(waypoints);
        List<Location> route = new ArrayList<>();
        Location current = start;

        while (!unvisited.isEmpty()) {
            Location nearest = findNearestLocation(current, unvisited);
            route.add(nearest);
            unvisited.remove(nearest);
            current = nearest;
        }

        return route;
    }

    private Location findNearestLocation(Location from, List<Location> candidates) {
        Location nearest = candidates.get(0);
        double minDistance = calculateDistance(from, nearest);

        for (Location candidate : candidates) {
            double distance = calculateDistance(from, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = candidate;
            }
        }

        return nearest;
    }

    /**
     * Haversine formula for distance calculation
     */
    private double calculateDistance(Location loc1, Location loc2) {
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

    private double calculateTotalDistance(List<Location> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += calculateDistance(route.get(i), route.get(i + 1));
        }
        return Math.round(total * 100.0) / 100.0;
    }

    private Double calculateSavings(RouteOptimizationRequest request, double optimizedDistance) {
        if (request.getWaypoints() == null || request.getWaypoints().isEmpty()) {
            return 0.0;
        }

        // Calculate sequential route distance
        List<Location> sequential = new ArrayList<>();
        sequential.add(request.getStartLocation());
        sequential.addAll(request.getWaypoints());
        if (request.getEndLocation() != null) {
            sequential.add(request.getEndLocation());
        }

        double sequentialDistance = calculateTotalDistance(sequential);
        double savings = ((sequentialDistance - optimizedDistance) / sequentialDistance) * 100;
        
        return Math.max(0, Math.round(savings * 100.0) / 100.0);
    }
}
