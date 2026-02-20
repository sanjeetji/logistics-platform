package com.logistics.routing.simulation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Route Simulation Service
 * 
 * Simulates route execution for validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteSimulationService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Simulate route execution
     */
    public RouteSimulationResponse simulateRoute(RouteSimulationRequest request) {
        log.info("Simulating route: {}", request.getRouteId());

        List<RouteSimulationResponse.SimulationStep> steps = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        LocalDateTime currentTime = LocalDateTime.now();
        double currentLat = request.getStartLatitude();
        double currentLon = request.getStartLongitude();
        double totalDistance = 0.0;
        long totalDuration = 0L;
        
        for (int i = 0; i < request.getStops().size(); i++) {
            RouteSimulationRequest.SimulationStop stop = request.getStops().get(i);
            
            // Calculate distance from previous location
            double distance = calculateDistance(currentLat, currentLon, stop.getLatitude(), stop.getLongitude());
            
            // Calculate travel time
            long travelMinutes = (long) ((distance / request.getSpeedKmh()) * 60);
            
            // Add traffic if enabled
            if (Boolean.TRUE.equals(request.getIncludeTraffic())) {
                travelMinutes = (long) (travelMinutes * 1.2); // 20% traffic buffer
            }
            
            LocalDateTime arrivalTime = currentTime.plusMinutes(travelMinutes);
            LocalDateTime departureTime = arrivalTime.plusMinutes(stop.getServiceTimeMinutes());
            
            RouteSimulationResponse.SimulationStep step = RouteSimulationResponse.SimulationStep.builder()
                .stepNumber(i + 1)
                .stopId(stop.getStopId())
                .latitude(stop.getLatitude())
                .longitude(stop.getLongitude())
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .distanceFromPreviousKm(distance)
                .durationFromPreviousMinutes(travelMinutes)
                .build();
            
            steps.add(step);
            
            // Update for next iteration
            currentLat = stop.getLatitude();
            currentLon = stop.getLongitude();
            currentTime = departureTime;
            totalDistance += distance;
            totalDuration += travelMinutes + stop.getServiceTimeMinutes();
            
            // Validate constraints
            if (totalDuration > 480) { // 8 hours
                warnings.add("Route exceeds 8-hour limit at stop " + (i + 1));
            }
        }
        
        boolean isValid = violations.isEmpty();
        
        RouteSimulationResponse response = RouteSimulationResponse.builder()
            .simulationId(UUID.randomUUID().toString())
            .routeId(request.getRouteId())
            .status(isValid ? RouteSimulationResponse.SimulationStatus.SUCCESS : 
                    RouteSimulationResponse.SimulationStatus.FAILED)
            .steps(steps)
            .totalDistanceKm(totalDistance)
            .totalDurationMinutes(totalDuration)
            .estimatedStartTime(LocalDateTime.now())
            .estimatedEndTime(LocalDateTime.now().plusMinutes(totalDuration))
            .isValid(isValid)
            .violations(violations)
            .warnings(warnings)
            .build();
        
        log.info("Route simulation completed: distance={}km, duration={}min, valid={}", 
            String.format("%.2f", totalDistance), totalDuration, isValid);
        
        return response;
    }

    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
}
