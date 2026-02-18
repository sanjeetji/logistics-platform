package com.logistics.routing.rerouting;

import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.dto.ReRoutingResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
import com.logistics.routing.feign.OrderServiceClient;
import com.logistics.routing.notification.DriverNotificationService;
import com.logistics.routing.solver.VRPSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dynamic Re-Routing Service
 * 
 * Handles real-time route optimization based on various triggers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicReRoutingService {

    private final VRPSolver vrpSolver;
    private final DriverNotificationService driverNotificationService;
    private final com.logistics.routing.repository.ETAPredictionRepository etaPredictionRepository;

    @Value("${routing.optimization.rerouting.enabled:true}")
    private boolean reRoutingEnabled;

    @Value("${routing.optimization.rerouting.min-delay-threshold-minutes:10}")
    private int minDelayThresholdMinutes;

    /**
     * Trigger re-routing based on event
     */
    public ReRoutingResponse triggerReRouting(ReRoutingRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("Re-routing triggered: route={}, trigger={}, driver={}",
                request.getRouteId(), request.getTrigger(), request.getDriverId());

        if (!reRoutingEnabled) {
            log.warn("Re-routing is disabled");
            return buildNoChangeResponse(request, startTime);
        }

        try {
            // Evaluate if re-routing is necessary
            if (!shouldReRoute(request)) {
                log.info("Re-routing not necessary for trigger: {}", request.getTrigger());
                return buildNoChangeResponse(request, startTime);
            }

            // Build optimization request from current state
            RouteOptimizationRequest optimizationRequest = buildOptimizationRequest(request);

            // Solve new route
            RouteOptimizationResponse optimizationResponse = vrpSolver.solve(optimizationRequest);

            if (optimizationResponse.getStatus() == RouteOptimizationResponse.OptimizationStatus.COMPLETED) {
                // Build re-routing response
                ReRoutingResponse response = buildSuccessResponse(request, optimizationResponse, startTime);

                // Save predictions for feedback loop
                savePredictions(response.getRouteId(), optimizationResponse);

                // Notify driver
                notifyDriver(request, response);

                log.info("Re-routing successful: route={}, stops={}, delay={}s",
                        request.getRouteId(),
                        response.getTotalStops(),
                        response.getDelaySeconds());

                return response;
            } else {
                log.warn("Re-routing optimization failed");
                return buildFailedResponse(request, startTime);
            }

        } catch (Exception e) {
            log.error("Error during re-routing", e);
            return buildFailedResponse(request, startTime);
        }
    }

    /**
     * Determine if re-routing is necessary based on trigger
     */
    private boolean shouldReRoute(ReRoutingRequest request) {
        switch (request.getTrigger()) {
            case TRAFFIC_INCIDENT:
                // Re-route if significant traffic delay expected
                return true;

            case DRIVER_DELAY:
                // Re-route if delay exceeds threshold
                return true;

            case NEW_URGENT_ORDER:
                // Always re-route for urgent orders
                return request.getUrgentOrderId() != null;

            case DELIVERY_FAILURE:
                // Re-route to reschedule failed delivery
                return request.getFailedStopId() != null;

            case DRIVER_BREAK:
                // Re-route to accommodate mandatory break
                return request.getBreakDurationMinutes() != null && request.getBreakDurationMinutes() > 0;

            case WEATHER_ALERT:
                // Re-route to avoid severe weather
                return true;

            default:
                return false;
        }
    }

    private final OrderServiceClient orderServiceClient;

    /**
     * Build optimization request from re-routing request
     */
    private RouteOptimizationRequest buildOptimizationRequest(ReRoutingRequest request) {
        RouteOptimizationRequest optimizationRequest = new RouteOptimizationRequest();
        optimizationRequest.setTenantId("default"); // Should come from context

        // Build stops from remaining stops
        List<RouteOptimizationRequest.DeliveryStop> stops = new ArrayList<>();
        for (String stopId : request.getRemainingStopIds()) {
            try {
                // Fetch latest order details
                // Assuming stopId maps to OrderId for now (or a known mapping exists)
                com.logistics.platform.common.dto.response.ApiResponse<com.logistics.routing.dto.ExternalOrderDto> response = orderServiceClient
                        .getOrderByOrderId(stopId);

                if (response != null && response.getData() != null) {
                    com.logistics.routing.dto.ExternalOrderDto order = response.getData();
                    RouteOptimizationRequest.DeliveryStop stop = new RouteOptimizationRequest.DeliveryStop();
                    stop.setStopId(stopId);
                    stop.setOrderId(order.getOrderId());

                    if (order.getDropLocation() != null) {
                        stop.setLatitude(order.getDropLocation().getLatitude());
                        stop.setLongitude(order.getDropLocation().getLongitude());
                        stop.setAddress(order.getDropLocation().getAddress());
                    }

                    // Default values if not in order
                    stop.setServiceDurationMinutes(10);
                    stop.setDemandWeight(10);
                    stops.add(stop);
                } else {
                    log.warn("Could not fetch order details for stop ID: {}", stopId);
                }
            } catch (Exception e) {
                log.error("Error fetching order details for stop ID: {}", stopId, e);
                // Continue with other stops or fail? For now, log and continue.
            }
        }

        // Add urgent order if applicable
        if (request.getTrigger() == com.logistics.routing.rerouting.ReRoutingTrigger.NEW_URGENT_ORDER
                && request.getUrgentOrderId() != null) {
            try {
                com.logistics.platform.common.dto.response.ApiResponse<com.logistics.routing.dto.ExternalOrderDto> response = orderServiceClient
                        .getOrderByOrderId(request.getUrgentOrderId());

                if (response != null && response.getData() != null) {
                    com.logistics.routing.dto.ExternalOrderDto urgentOrder = response.getData();
                    RouteOptimizationRequest.DeliveryStop urgentStop = new RouteOptimizationRequest.DeliveryStop();
                    urgentStop.setStopId(request.getUrgentOrderId());
                    urgentStop.setOrderId(urgentOrder.getOrderId());

                    if (urgentOrder.getDropLocation() != null) {
                        urgentStop.setLatitude(urgentOrder.getDropLocation().getLatitude());
                        urgentStop.setLongitude(urgentOrder.getDropLocation().getLongitude());
                        urgentStop.setAddress(urgentOrder.getDropLocation().getAddress());
                    }

                    urgentStop.setPriority(10); // High priority
                    urgentStop.setServiceDurationMinutes(10);
                    urgentStop.setDemandWeight(10);
                    stops.add(urgentStop);
                }
            } catch (Exception e) {
                log.error("Error fetching urgent order details: {}", request.getUrgentOrderId(), e);
            }
        }

        optimizationRequest.setStops(stops);

        // Build vehicle
        List<RouteOptimizationRequest.Vehicle> vehicles = new ArrayList<>();
        RouteOptimizationRequest.Vehicle vehicle = new RouteOptimizationRequest.Vehicle();
        vehicle.setVehicleId(request.getVehicleId());
        vehicle.setDriverId(request.getDriverId());
        vehicle.setStartLatitude(request.getCurrentLatitude());
        vehicle.setStartLongitude(request.getCurrentLongitude());
        // Configure vehicle capacity if known, else default
        vehicle.setCapacityWeight(1000);
        vehicles.add(vehicle);

        optimizationRequest.setVehicles(vehicles);

        // Set constraints
        RouteOptimizationRequest.OptimizationConstraints constraints = new RouteOptimizationRequest.OptimizationConstraints();
        constraints.setRespectTimeWindows(
                request.getPreserveTimeWindows() != null ? request.getPreserveTimeWindows() : true);
        constraints.setRespectCapacity(true);
        optimizationRequest.setConstraints(constraints);

        return optimizationRequest;
    }

    /**
     * Build success response
     */
    private ReRoutingResponse buildSuccessResponse(ReRoutingRequest request,
            RouteOptimizationResponse optimizationResponse,
            long startTime) {

        RouteOptimizationResponse.OptimizedRoute newRoute = optimizationResponse.getRoutes().isEmpty()
                ? null
                : optimizationResponse.getRoutes().get(0);

        return ReRoutingResponse.builder()
                .reRoutingId(UUID.randomUUID().toString())
                .routeId(request.getRouteId())
                .status(ReRoutingResponse.ReRoutingStatus.SUCCESS)
                .newStops(newRoute != null ? newRoute.getStops() : new ArrayList<>())
                .totalStops(newRoute != null ? newRoute.getStops().size() : 0)
                .stopsAdded(calculateStopsAdded(request, newRoute))
                .stopsRemoved(0)
                .stopsReordered(calculateStopsReordered(request, newRoute))
                .delaySeconds(0L) // Calculate from ETA comparison
                .driverNotified(false) // Will be set after notification
                .reRoutedAt(LocalDateTime.now())
                .computationTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * Build no change response
     */
    private ReRoutingResponse buildNoChangeResponse(ReRoutingRequest request, long startTime) {
        return ReRoutingResponse.builder()
                .reRoutingId(UUID.randomUUID().toString())
                .routeId(request.getRouteId())
                .status(ReRoutingResponse.ReRoutingStatus.NO_CHANGE_NEEDED)
                .totalStops(request.getRemainingStopIds().size())
                .stopsAdded(0)
                .stopsRemoved(0)
                .stopsReordered(0)
                .delaySeconds(0L)
                .driverNotified(false)
                .reRoutedAt(LocalDateTime.now())
                .computationTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * Build failed response
     */
    private ReRoutingResponse buildFailedResponse(ReRoutingRequest request, long startTime) {
        return ReRoutingResponse.builder()
                .reRoutingId(UUID.randomUUID().toString())
                .routeId(request.getRouteId())
                .status(ReRoutingResponse.ReRoutingStatus.FAILED)
                .reRoutedAt(LocalDateTime.now())
                .computationTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * Notify driver of route change
     */
    private void notifyDriver(ReRoutingRequest request, ReRoutingResponse response) {
        try {
            String message = buildNotificationMessage(request, response);
            driverNotificationService.notifyDriver(request.getDriverId(), message, response);
            response.setDriverNotified(true);
            response.setNotificationMessage(message);
        } catch (Exception e) {
            log.error("Failed to notify driver", e);
            response.setDriverNotified(false);
        }
    }

    /**
     * Build notification message for driver
     */
    private String buildNotificationMessage(ReRoutingRequest request, ReRoutingResponse response) {
        switch (request.getTrigger()) {
            case TRAFFIC_INCIDENT:
                return String.format("Route updated due to traffic incident. %d stops reordered.",
                        response.getStopsReordered());

            case NEW_URGENT_ORDER:
                return String.format("Urgent delivery added to your route. %d total stops.",
                        response.getTotalStops());

            case DELIVERY_FAILURE:
                return "Route updated after delivery attempt. Please check new sequence.";

            case DRIVER_BREAK:
                return String.format("Route adjusted for your %d-minute break.",
                        request.getBreakDurationMinutes());

            case WEATHER_ALERT:
                return "Route updated to avoid severe weather area.";

            case DRIVER_DELAY:
                return "Route optimized based on current progress.";

            default:
                return "Your route has been updated.";
        }
    }

    /**
     * Calculate number of stops added
     */
    private int calculateStopsAdded(ReRoutingRequest request, RouteOptimizationResponse.OptimizedRoute newRoute) {
        if (newRoute == null)
            return 0;
        int originalStops = request.getRemainingStopIds().size();
        int newStops = newRoute.getStops().size();
        return Math.max(0, newStops - originalStops);
    }

    /**
     * Calculate number of stops reordered
     */
    private int calculateStopsReordered(ReRoutingRequest request, RouteOptimizationResponse.OptimizedRoute newRoute) {
        if (newRoute == null)
            return 0;
        // Simplified - in real implementation, compare sequences
        return newRoute.getStops().size();
    }

    /**
     * Save ETA predictions for ML feedback loop
     */
    private void savePredictions(String routeId, RouteOptimizationResponse optimizationResponse) {
        try {
            if (optimizationResponse.getRoutes() == null || optimizationResponse.getRoutes().isEmpty()) {
                return;
            }

            // Assuming single route for now
            RouteOptimizationResponse.OptimizedRoute route = optimizationResponse.getRoutes().get(0);
            if (route.getStops() == null)
                return;

            List<com.logistics.routing.model.ETAPrediction> predictions = new ArrayList<>();
            for (RouteOptimizationResponse.RouteStop stop : route.getStops()) {
                if (stop.getOrderId() != null && stop.getEstimatedArrival() != null) {
                    predictions.add(com.logistics.routing.model.ETAPrediction.builder()
                            .orderId(stop.getOrderId())
                            .routeId(routeId)
                            .predictedArrival(stop.getEstimatedArrival())
                            .createdAt(LocalDateTime.now())
                            .build());
                }
            }

            if (!predictions.isEmpty()) {
                etaPredictionRepository.saveAll(predictions);
                log.debug("Saved {} ETA predictions for route {}", predictions.size(), routeId);
            }
        } catch (Exception e) {
            log.error("Failed to save ETA predictions for route {}", routeId, e);
            // Don't fail the re-routing process
        }
    }
}
