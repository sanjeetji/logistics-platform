package com.logistics.dispatch.service;

import com.logistics.dispatch.client.MLServiceClient;
import com.logistics.dispatch.event.RouteOptimizedEvent;
import com.logistics.dispatch.model.DispatchAssignment;
import com.logistics.dispatch.repository.DispatchAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteOptimizationService {

    private final MLServiceClient mlServiceClient;
    private final DispatchAssignmentRepository assignmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void optimizeDriverRoutes(String hubId, Double lat, Double lon) {
        log.info("Triggering route optimization for hub {} at {}, {}", hubId, lat, lon);

        // 1. Fetch active assignments that can be re-routed (e.g. ASSIGNED or EN_ROUTE
        // state)
        List<DispatchAssignment> activeAssignments = assignmentRepository.findAll(); // Simplified for MVP

        if (activeAssignments.isEmpty()) {
            log.info("No active assignments to optimize.");
            return;
        }

        // 2. Prepare ML Request
        MLServiceClient.RouteOptimizationRequest request = MLServiceClient.RouteOptimizationRequest.builder()
                .depot(createLocation(lat, lon))
                .orders(activeAssignments.stream().map(this::toOrderLocation).collect(Collectors.toList()))
                .vehicles(activeAssignments.stream()
                        .map(a -> String.valueOf(a.getDriverId()))
                        .distinct()
                        .map(id -> toVehicleLocation(id, lat, lon))
                        .collect(Collectors.toList()))
                .build();

        try {
            // 3. Call ML Service
            MLServiceClient.RouteOptimizationResponse response = mlServiceClient.optimizeRoute(request);

            if ("OPTIMAL".equals(response.getStatus())) {
                log.info("Successfully optimized routes. Total distance: {}m", response.getTotalDistanceMeters());

                // 4. Update assignment sequences and publish event
                response.getRoutes().forEach(route -> {
                    log.info("New route for driver {}: {}", route.getVehicleId(), route.getRouteOrderIds());
                    // In a real scenario, we'd update sequence/priority in DB
                });

                eventPublisher.publishEvent(new RouteOptimizedEvent(this, response.getRoutes()));
            }
        } catch (Exception e) {
            log.error("Failed to optimize routes via ML service: {}", e.getMessage());
        }
    }

    private MLServiceClient.Location createLocation(Double lat, Double lon) {
        MLServiceClient.Location loc = new MLServiceClient.Location();
        loc.setLat(lat);
        loc.setLon(lon);
        return loc;
    }

    private MLServiceClient.OrderLocation toOrderLocation(DispatchAssignment assignment) {
        MLServiceClient.OrderLocation loc = new MLServiceClient.OrderLocation();
        loc.setId(assignment.getOrderId());
        // Lat/Lon dummies for now
        loc.setLat(28.62);
        loc.setLon(77.22);
        loc.setWeight(10.0);
        return loc;
    }

    private MLServiceClient.VehicleLocation toVehicleLocation(String driverId, Double lat, Double lon) {
        MLServiceClient.VehicleLocation loc = new MLServiceClient.VehicleLocation();
        loc.setId(driverId);
        loc.setLat(lat);
        loc.setLon(lon);
        loc.setCapacity(50.0); // Dummy
        return loc;
    }
}
