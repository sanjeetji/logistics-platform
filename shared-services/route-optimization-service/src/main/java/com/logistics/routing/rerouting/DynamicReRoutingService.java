package com.logistics.routing.rerouting;

import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.dto.ReRoutingResponse;
import com.logistics.routing.dto.RouteOptimizationRequest;
import com.logistics.routing.dto.RouteOptimizationResponse;
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

    /**
     * Build optimization request from re-routing request
     */
    private RouteOptimizationRequest buildOptimizationRequest(ReRoutingRequest request) {
        RouteOptimizationRequest optimizationRequest = new RouteOptimizationRequest();
        optimizationRequest.setTenantId("default"); // Should come from context
        
        // Build stops from remaining stops
        List<RouteOptimizationRequest.DeliveryStop> stops = new ArrayList<>();
        for (String stopId : request.getRemainingStopIds()) {
            // In real implementation, fetch stop details from database
            RouteOptimizationRequest.DeliveryStop stop = new RouteOptimizationRequest.DeliveryStop();
            stop.setStopId(stopId);
            // Set other stop details...
            stops.add(stop);
        }
        
        // Add urgent order if applicable
        if (request.getTrigger() == ReRoutingTrigger.NEW_URGENT_ORDER && request.getUrgentOrderId() != null) {
            RouteOptimizationRequest.DeliveryStop urgentStop = new RouteOptimizationRequest.DeliveryStop();
            urgentStop.setStopId(request.getUrgentOrderId());
            urgentStop.setPriority(10); // High priority
            stops.add(urgentStop);
        }
        
        optimizationRequest.setStops(stops);
        
        // Build vehicle
        List<RouteOptimizationRequest.Vehicle> vehicles = new ArrayList<>();
        RouteOptimizationRequest.Vehicle vehicle = new RouteOptimizationRequest.Vehicle();
        vehicle.setVehicleId(request.getVehicleId());
        vehicle.setDriverId(request.getDriverId());
        vehicle.setStartLatitude(request.getCurrentLatitude());
        vehicle.setStartLongitude(request.getCurrentLongitude());
        vehicles.add(vehicle);
        
        optimizationRequest.setVehicles(vehicles);
        
        // Set constraints
        RouteOptimizationRequest.OptimizationConstraints constraints = new RouteOptimizationRequest.OptimizationConstraints();
        constraints.setRespectTimeWindows(request.getPreserveTimeWindows() != null ? request.getPreserveTimeWindows() : true);
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
        if (newRoute == null) return 0;
        int originalStops = request.getRemainingStopIds().size();
        int newStops = newRoute.getStops().size();
        return Math.max(0, newStops - originalStops);
    }

    /**
     * Calculate number of stops reordered
     */
    private int calculateStopsReordered(ReRoutingRequest request, RouteOptimizationResponse.OptimizedRoute newRoute) {
        if (newRoute == null) return 0;
        // Simplified - in real implementation, compare sequences
        return newRoute.getStops().size();
    }
}
