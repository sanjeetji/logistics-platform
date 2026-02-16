package com.logistics.routing.service;

import com.logistics.routing.dto.LocationDTO;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.model.Route;
import com.logistics.routing.model.Waypoint;
import com.logistics.routing.model.WaypointType;
import com.logistics.routing.repository.RouteRepository;
import com.logistics.routing.repository.WaypointRepository;
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
public class RoutePlanningService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RoutePlanningService.class);

    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;
    private final RouteOptimizationService optimizationService;

    public RoutePlanningService(RouteRepository routeRepository,
            WaypointRepository waypointRepository,
            RouteOptimizationService optimizationService) {
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
        this.optimizationService = optimizationService;
    }

    /**
     * Create optimized routes for multiple vehicles
     */
    /**
     * Create optimized routes for multiple vehicles
     */
    @Transactional
    public List<Route> createOptimizedRoutes(RouteOptimizationRequest request) {
        log.info("Creating optimized routes for {} orders and {} vehicles",
                request.getOrderIds().size(), request.getVehicleIds().size());

        // Mock order locations (in production, fetch from B2B order service)
        List<OrderLocation> orderLocations = mockOrderLocations(request.getOrderIds());

        // Convert to Shipments
        List<com.logistics.routing.dto.ShipmentDTO> shipments = new ArrayList<>();
        int sIdx = 0;
        for (OrderLocation order : orderLocations) {
            shipments.add(com.logistics.routing.dto.ShipmentDTO.builder()
                    .id("shipment_" + sIdx++)
                    .pickupLocation(
                            new LocationDTO(order.getPickupLat(), order.getPickupLon(), "Pickup " + order.getOrderId()))
                    .deliveryLocation(new LocationDTO(order.getDeliveryLat(), order.getDeliveryLon(),
                            "Delivery " + order.getOrderId()))
                    .weight(10) // Mock weight
                    .build());
        }
        request.setShipments(shipments);

        // Convert Vehicles
        List<com.logistics.routing.dto.VehicleDTO> vehicles = new ArrayList<>();
        for (Long vId : request.getVehicleIds()) {
            vehicles.add(com.logistics.routing.dto.VehicleDTO.builder()
                    .id(vId)
                    .startLocation(request.getStartLocation())
                    .capacityWeight(1000)
                    .build());
        }
        request.setVehicles(vehicles);
        request.setOptimizationType(RouteOptimizationRequest.OptimizationType.VRP_JSPRIT);

        // Call Optimization Strategy
        com.logistics.routing.dto.RouteOptimizationResponse response = optimizationService.optimizeRoute(request);

        List<Route> routes = new ArrayList<>();

        if (response.getRoutes() != null) {
            for (com.logistics.routing.dto.RouteOptimizationResponse.OptimizedRouteDTO routeDTO : response.getRoutes()) {

                Route route = Route.builder()
                        .routeId("ROUTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                        .vehicleId(routeDTO.getVehicleId())
                        .routeDate(request.getRouteDate())
                        .totalDistance(routeDTO.getDistanceKm())
                        .estimatedDuration(0) // logic to calc duration
                        .build();

                LocalDateTime currentTime = LocalDateTime.now();
                int seq = 1;

                if (routeDTO.getWaypoints() != null) {
                    for (LocationDTO wpLoc : routeDTO.getWaypoints()) {
                        Waypoint waypoint = Waypoint.builder()
                                .waypointSequence(seq++)
                                .waypointType(seq == 2 ? WaypointType.DEPOT : WaypointType.PICKUP) // Simplified type
                                                                                                   // logic
                                .address(wpLoc.getAddress())
                                .latitude(wpLoc.getLatitude())
                                .longitude(wpLoc.getLongitude())
                                .estimatedArrival(currentTime.plusMinutes(seq * 15))
                                .serviceTime(10)
                                .build();
                        route.addWaypoint(waypoint);
                    }
                }

                route.calculateTotalDistance(); // Recalculate based on waypoints if needed
                routes.add(routeRepository.save(route));
            }
        }

        log.info("Created {} optimized routes", routes.size());
        return routes;
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

        // Extract waypoints
        List<Waypoint> waypoints = route.getWaypoints();

        RouteOptimizationRequest request = new RouteOptimizationRequest();
        request.setOptimizationType(RouteOptimizationRequest.OptimizationType.TSP_NEAREST_NEIGHBOR);

        if (!waypoints.isEmpty()) {
            request.setStartLocation(new LocationDTO(
                    waypoints.get(0).getLatitude(),
                    waypoints.get(0).getLongitude(),
                    waypoints.get(0).getAddress()));
        }

        List<LocationDTO> stops = new ArrayList<>();
        for (Waypoint wp : waypoints) {
            if (wp.getWaypointType() != WaypointType.DEPOT) {
                stops.add(new LocationDTO(wp.getLatitude(), wp.getLongitude(), wp.getAddress()));
            }
        }
        request.setWaypoints(stops);
        request.setReturnToStart(true);

        // Optimize
        com.logistics.routing.dto.RouteOptimizationResponse response = optimizationService.optimizeRoute(request);

        // Apply new sequence
        if (response.getRoutes() != null && !response.getRoutes().isEmpty()) {
            com.logistics.routing.dto.RouteOptimizationResponse.OptimizedRouteDTO optimalRoute = response.getRoutes()
                    .get(0);

            // For now, let's just save the new total distance.
            route.setTotalDistance(optimalRoute.getDistanceKm());
        }

        route.calculateTotalDistance(); // Recalculate based on waypoints if needed
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
                    77.5 + random.nextDouble() * 0.2));
        }

        return locations;
    }

    private static class OrderLocation {
        private String orderId;
        private double pickupLat;
        private double pickupLon;
        private double deliveryLat;
        private double deliveryLon;

        public OrderLocation(String orderId, double pickupLat, double pickupLon, double deliveryLat,
                double deliveryLon) {
            this.orderId = orderId;
            this.pickupLat = pickupLat;
            this.pickupLon = pickupLon;
            this.deliveryLat = deliveryLat;
            this.deliveryLon = deliveryLon;
        }

        public String getOrderId() {
            return orderId;
        }

        public double getPickupLat() {
            return pickupLat;
        }

        public double getPickupLon() {
            return pickupLon;
        }

        public double getDeliveryLat() {
            return deliveryLat;
        }

        public double getDeliveryLon() {
            return deliveryLon;
        }
    }
}
