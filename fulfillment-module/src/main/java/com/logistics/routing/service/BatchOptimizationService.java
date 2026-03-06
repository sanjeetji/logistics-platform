package com.logistics.routing.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderStatus;
import com.logistics.order.model.OrderStop;
import com.logistics.order.repository.OrderRepository;
import com.logistics.platform.api.fleet.FleetClient;
import com.logistics.platform.common.dto.fleet.DriverDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.solver.VRPSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Batch Optimization Service
 * 
 * Orchestrates multi-vehicle, multi-order routing optimization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchOptimizationService {

    private final OrderRepository orderRepository;
    private final FleetClient fleetClient;
    private final VRPSolver vrpSolver;

    /**
     * Optimize all pending orders for a tenant using available drivers in the area
     */
    @Transactional
    public RouteOptimizationResponse optimizeBatch(String tenantId, Double lat, Double lon, Double radius) {
        log.info("Starting batch optimization for tenant: {} at ({}, {}) radius: {}m", tenantId, lat, lon, radius);

        // 1. Fetch pending orders (ASSIGNED or CREATED)
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> tenantId.equals(o.getTenantId()))
                .filter(o -> o.getStatus() == OrderStatus.CREATED || o.getStatus() == OrderStatus.ASSIGNED)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            log.info("No pending orders found for batch optimization");
            return RouteOptimizationResponse.builder()
                    .status(RouteOptimizationResponse.OptimizationStatus.FAILED)
                    .message("No pending orders found")
                    .build();
        }

        // 2. Fetch available drivers
        ApiResponse<List<DriverDto>> driverResponse = fleetClient.findNearestAvailableDrivers(lat, lon, radius);
        List<DriverDto> availableDrivers = (driverResponse != null && driverResponse.getData() != null)
                ? driverResponse.getData()
                : new ArrayList<>();

        if (availableDrivers.isEmpty()) {
            log.warn("No available drivers found for batch optimization");
            return RouteOptimizationResponse.builder()
                    .status(RouteOptimizationResponse.OptimizationStatus.FAILED)
                    .message("No available drivers found")
                    .build();
        }

        // 3. Best-Fit Logic if capacity is exceeded
        List<Order> eligibleOrders = applyBestFitHeuristic(orders, availableDrivers);
        List<String> unassignedOrderIds = orders.stream()
                .filter(o -> !eligibleOrders.contains(o))
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        // 4. Build Request
        RouteOptimizationRequest request = buildRequest(tenantId, eligibleOrders, availableDrivers);

        // 5. Solve
        RouteOptimizationResponse response = vrpSolver.solve(request);
        response.setUnassignedOrderIds(unassignedOrderIds);

        // 6. Apply Results if completed
        if (response.getStatus() == RouteOptimizationResponse.OptimizationStatus.COMPLETED) {
            applyResults(eligibleOrders, response);
        }

        return response;
    }

    private List<Order> applyBestFitHeuristic(List<Order> orders, List<DriverDto> drivers) {
        final double totalCapacity = drivers.size() * 1000.0; // Assume 1000kg per vehicle for now

        double totalDemand = orders.stream()
                .mapToDouble(o -> o.getWeightKg() != null ? o.getWeightKg() : 10.0)
                .sum();

        if (totalDemand <= totalCapacity) {
            return orders;
        }

        log.info("Capacity exceeded (Demand: {}kg, Capacity: {}kg). Applying best-fit heuristic.", totalDemand,
                totalCapacity);

        // Sort by priority (desc), then by weight (asc)
        return orders.stream()
                .sorted(Comparator.comparing(Order::getPriority).reversed()
                        .thenComparing(o -> o.getWeightKg() != null ? o.getWeightKg() : 10.0))
                .filter(new Predicate<Order>() {
                    double currentWeight = 0;

                    @Override
                    public boolean test(Order o) {
                        double weight = o.getWeightKg() != null ? o.getWeightKg() : 10.0;
                        if (currentWeight + weight <= totalCapacity) {
                            currentWeight += weight;
                            return true;
                        }
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private RouteOptimizationRequest buildRequest(String tenantId, List<Order> orders, List<DriverDto> drivers) {
        List<RouteOptimizationRequest.DeliveryStop> stops = new ArrayList<>();

        for (Order order : orders) {
            // Each stop of each order becomes a VRP stop
            for (OrderStop stop : order.getStops()) {
                if (!stop.getCompleted()) {
                    stops.add(RouteOptimizationRequest.DeliveryStop.builder()
                            .stopId(String.valueOf(stop.getId()))
                            .orderId(order.getOrderId())
                            .latitude(stop.getLocation().getLatitude())
                            .longitude(stop.getLocation().getLongitude())
                            .address(stop.getLocation().getAddress())
                            .timeWindowStart(stop.getEstimatedArrival())
                            .timeWindowEnd(
                                    stop.getEstimatedArrival() != null ? stop.getEstimatedArrival().plusHours(2) : null)
                            .demandWeight(10) // Default
                            .serviceDurationMinutes(10)
                            .build());
                }
            }
        }

        List<RouteOptimizationRequest.Vehicle> vehicles = drivers
                .stream().<RouteOptimizationRequest.Vehicle>map(d -> RouteOptimizationRequest.Vehicle.builder()
                        .vehicleId("V-" + d.getId())
                        .driverId(String.valueOf(d.getId()))
                        .startLatitude(d.getCurrentLatitude())
                        .startLongitude(d.getCurrentLongitude())
                        .shiftStart(LocalDateTime.now().withHour(8).withMinute(0))
                        .shiftEnd(LocalDateTime.now().withHour(20).withMinute(0))
                        .capacityWeight(1000) // Default
                        .build())
                .collect(Collectors.toList());

        return RouteOptimizationRequest.builder()
                .tenantId(tenantId)
                .stops(stops)
                .vehicles(vehicles)
                .constraints(RouteOptimizationRequest.OptimizationConstraints.builder()
                        .respectTimeWindows(true)
                        .respectCapacity(true)
                        .build())
                .build();
    }

    private void applyResults(List<Order> orders, RouteOptimizationResponse response) {
        log.info("Applying batch optimization results to {} orders", orders.size());

        for (RouteOptimizationResponse.OptimizedRoute route : response.getRoutes()) {
            String driverId = route.getDriverId();
            String vehicleId = route.getVehicleId();

            for (int i = 0; i < route.getStops().size(); i++) {
                RouteOptimizationResponse.RouteStop routeStop = route.getStops().get(i);
                final int sequence = i + 1;

                // Find and update the order stop
                orders.stream()
                        .filter(o -> o.getOrderId().equals(routeStop.getOrderId()))
                        .findFirst()
                        .ifPresent(o -> {
                            o.setDriverId(driverId);
                            o.setVehicleId(vehicleId);
                            o.setStatus(OrderStatus.ASSIGNED);

                            o.getStops().stream()
                                    .filter(s -> String.valueOf(s.getId()).equals(routeStop.getStopId()))
                                    .findFirst()
                                    .ifPresent(s -> s.setStopSequence(sequence));
                        });
            }
        }

        orderRepository.saveAll(orders);
        log.info("Batch optimization results applied successfully");
    }
}
