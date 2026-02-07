package com.logistics.route.service;

import com.logistics.route.dto.LocationDTO;
import com.logistics.route.dto.RouteOptimizationRequest;
import com.logistics.route.model.Route;
import com.logistics.route.model.Waypoint;
import com.logistics.route.model.WaypointType;
import com.logistics.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for route planning and management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoutePlanningService {

    private final RouteRepository routeRepository;
    private final RouteOptimizationService optimizationService;
    private final DistanceMatrixService distanceMatrixService;

    /**
     * Create optimized routes for multiple vehicles
     */
    @Transactional
    public List<Route> createOptimizedRoutes(RouteOptimizationRequest request) {
        log.info("Creating optimized routes for {} orders and {} vehicles", 
                request.getOrderIds().size(), request.getVehicleIds().size());

        List<Route> routes = new ArrayList<>();

        // Mock order locations (in production, fetch from B2B order service)
        List<OrderLocation> orderLocations = mockOrderLocations(request.getOrderIds());

        // Simple assignment: distribute orders evenly across vehicles
        int ordersPerVehicle = (int) Math.ceil((double) orderLocations.size() / request.getVehicleIds().size());

        for (int v = 0; v < request.getVehicleIds().size(); v++) {
            int startIdx = v * ordersPerVehicle;
            int endIdx = Math.min(startIdx + ordersPerVehicle, orderLocations.size());

            if (startIdx >= orderLocations.size()) break;

            List<OrderLocation> vehicleOrders = orderLocations.subList(startIdx, endIdx);
            Route route = createRouteForVehicle(
                    request.getVehicleIds().get(v),
                    vehicleOrders,
                    request.getStartLocation(),
                    request.getRouteDate()
            );
            routes.add(route);
        }

        log.info("Created {} optimized routes", routes.size());
        return routes;
    }

    /**
     * Create route for single vehicle
     */
    private Route createRouteForVehicle(Long vehicleId, List<OrderLocation> orders, 
                                       LocationDTO startLocation, java.time.LocalDate routeDate) {
        
        // Build coordinates array (depot + all order locations)
        int n = orders.size() * 2 + 1; // depot + pickup + delivery for each order
        double[][] coordinates = new double[n][2];
        
        coordinates[0][0] = startLocation.getLatitude();
        coordinates[0][1] = startLocation.getLongitude();

        int idx = 1;
        for (OrderLocation order : orders) {
            coordinates[idx][0] = order.getPickupLat();
            coordinates[idx][1] = order.getPickupLon();
            idx++;
            coordinates[idx][0] = order.getDeliveryLat();
            coordinates[idx][1] = order.getDeliveryLon();
            idx++;
        }

        // Build distance matrix
        double[][] distanceMatrix = distanceMatrixService.buildDistanceMatrix(coordinates);

        // Optimize route
        List<Integer> optimizedSequence = optimizationService.optimizeRoute(distanceMatrix, 0);

        // Create route entity
        Route route = Route.builder()
                .routeId("ROUTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .vehicleId(vehicleId)
                .routeDate(routeDate)
                .totalDistance(0.0)
                .estimatedDuration(0)
                .build();

        // Create waypoints
        LocalDateTime currentTime = LocalDateTime.now();
        for (int i = 0; i < optimizedSequence.size(); i++) {
            int locationIdx = optimizedSequence.get(i);
            
            Waypoint waypoint = Waypoint.builder()
                    .waypointSequence(i + 1)
                    .waypointType(locationIdx == 0 ? WaypointType.DEPOT : 
                                 (locationIdx % 2 == 1 ? WaypointType.PICKUP : WaypointType.DELIVERY))
                    .address(locationIdx == 0 ? startLocation.getAddress() : "Order Location")
                    .latitude(coordinates[locationIdx][0])
                    .longitude(coordinates[locationIdx][1])
                    .estimatedArrival(currentTime.plusMinutes(i * 15))
                    .serviceTime(10)
                    .build();

            // Calculate distance from previous waypoint
            if (i > 0) {
                int prevIdx = optimizedSequence.get(i - 1);
                waypoint.setDistanceFromPrevious(distanceMatrix[prevIdx][locationIdx]);
            }

            route.addWaypoint(waypoint);
        }

        // Calculate totals
        route.calculateTotalDistance();
        route.calculateEstimatedDuration();

        // Calculate optimization score
        double naiveDistance = calculateNaiveDistance(distanceMatrix);
        route.setOptimizationScore(
                optimizationService.calculateOptimizationScore(route.getTotalDistance(), naiveDistance)
        );

        return routeRepository.save(route);
    }

    /**
     * Get route by ID
     */
    public Route getRouteById(String routeId) {
        return routeRepository.findByRouteId(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found: " + routeId));
    }

    /**
     * Get driver's routes
     */
    public List<Route> getDriverRoutes(Long driverId) {
        return routeRepository.findByDriverId(driverId);
    }

    /**
     * Re-optimize existing route
     */
    @Transactional
    public Route reoptimizeRoute(String routeId) {
        Route route = getRouteById(routeId);
        
        // Extract coordinates
        List<Waypoint> waypoints = route.getWaypoints();
        double[][] coordinates = new double[waypoints.size()][2];
        
        for (int i = 0; i < waypoints.size(); i++) {
            coordinates[i][0] = waypoints.get(i).getLatitude();
            coordinates[i][1] = waypoints.get(i).getLongitude();
        }

        // Rebuild distance matrix and optimize
        double[][] distanceMatrix = distanceMatrixService.buildDistanceMatrix(coordinates);
        List<Integer> optimizedSequence = optimizationService.optimizeRoute(distanceMatrix, 0);

        // Update waypoint sequences
        for (int i = 0; i < optimizedSequence.size(); i++) {
            waypoints.get(optimizedSequence.get(i)).setWaypointSequence(i + 1);
        }

        route.calculateTotalDistance();
        route.calculateEstimatedDuration();

        return routeRepository.save(route);
    }

    /**
     * Calculate naive (non-optimized) distance
     */
    private double calculateNaiveDistance(double[][] distanceMatrix) {
        double distance = 0;
        for (int i = 0; i < distanceMatrix.length - 1; i++) {
            distance += distanceMatrix[i][i + 1];
        }
        return distance;
    }

    /**
     * Mock order locations (replace with actual B2B order service call)
     */
    private List<OrderLocation> mockOrderLocations(List<String> orderIds) {
        List<OrderLocation> locations = new ArrayList<>();
        Random random = new Random();
        
        for (String orderId : orderIds) {
            locations.add(new OrderLocation(
                    orderId,
                    12.9 + random.nextDouble() * 0.2,
                    77.5 + random.nextDouble() * 0.2,
                    12.9 + random.nextDouble() * 0.2,
                    77.5 + random.nextDouble() * 0.2
            ));
        }
        
        return locations;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class OrderLocation {
        private String orderId;
        private double pickupLat;
        private double pickupLon;
        private double deliveryLat;
        private double deliveryLon;
    }
}
