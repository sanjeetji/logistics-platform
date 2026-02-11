package com.logistics.dispatch.service;

import com.logistics.dispatch.dto.DispatchRequest;
import com.logistics.dispatch.dto.DriverScore;
import com.logistics.dispatch.model.AssignmentStatus;
import com.logistics.dispatch.model.DispatchAssignment;
import com.logistics.dispatch.repository.DispatchAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Core dispatch service for order-to-driver assignment
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchService {

    private final DispatchAssignmentRepository assignmentRepository;
    private final DistanceCalculationService distanceService;
    private final RestTemplate restTemplate;

    // Service URLs (should be from config)
    private static final String FLEET_SERVICE_URL = "http://fleet-service:8083/api/v1";
    private static final String ORDER_SERVICE_URL = "http://order-service:8081/api/v1";

    /**
     * Find best driver for an order using scoring algorithm
     */
    public DriverScore findBestDriver(DispatchRequest request) {
        log.info("Finding best driver for order: {}", request.getOrderId());

        // Get available drivers from fleet service
        List<DriverScore> availableDrivers = getAvailableDriversWithScores(request);

        if (availableDrivers.isEmpty()) {
            log.warn("No available drivers found for order: {}", request.getOrderId());
            return null;
        }

        // Sort by score (highest first)
        availableDrivers.sort(Comparator.comparingDouble(DriverScore::getScore).reversed());

        DriverScore bestDriver = availableDrivers.get(0);
        log.info("Best driver found: {} with score: {}", bestDriver.getDriverId(), bestDriver.getScore());

        return bestDriver;
    }

    /**
     * Assign order to a specific driver
     */
    @Transactional
    public DispatchAssignment assignOrderToDriver(String orderId, Long driverId, Long vehicleId) {
        log.info("Assigning order {} to driver {} with vehicle {}", orderId, driverId, vehicleId);

        // Check if order already has an active assignment
        Optional<DispatchAssignment> existing = assignmentRepository.findByOrderIdAndStatus(
                orderId, AssignmentStatus.PENDING);

        if (existing.isPresent()) {
            log.warn("Order {} already has a pending assignment", orderId);
            throw new IllegalStateException("Order already has a pending assignment");
        }

        DispatchAssignment assignment = DispatchAssignment.builder()
                .orderId(orderId)
                .driverId(driverId)
                .vehicleId(vehicleId)
                .status(AssignmentStatus.AUTO_ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .acceptedAt(LocalDateTime.now())
                .build();

        DispatchAssignment saved = assignmentRepository
                .save(Objects.requireNonNull(assignment, "DispatchAssignment must not be null"));

        // Call order-service to update order with driver assignment
        try {
            updateOrderAssignment(orderId, driverId, vehicleId);
        } catch (Exception e) {
            log.error("Failed to update order service: {}", e.getMessage());
            // Continue anyway - assignment is saved
        }

        // Call fleet-service to update driver status
        try {
            updateDriverAssignment(driverId, orderId, vehicleId);
        } catch (Exception e) {
            log.error("Failed to update fleet service: {}", e.getMessage());
        }

        log.info("Assignment created successfully: {}", saved.getId());
        return saved;
    }

    /**
     * Auto-dispatch: Find best driver and assign automatically
     */
    @Transactional
    public DispatchAssignment autoDispatch(DispatchRequest request) {
        log.info("Auto-dispatching order: {}", request.getOrderId());

        DriverScore bestDriver = findBestDriver(request);

        if (bestDriver == null) {
            throw new RuntimeException("No available drivers found");
        }

        return assignOrderToDriver(request.getOrderId(), bestDriver.getDriverId(), null);
    }

    /**
     * Get assignment by order ID
     */
    public Optional<DispatchAssignment> getAssignmentByOrderId(String orderId) {
        return assignmentRepository.findByOrderId(orderId);
    }

    /**
     * Cancel assignment
     */
    @Transactional
    public DispatchAssignment cancelAssignment(String orderId) {
        DispatchAssignment assignment = assignmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(AssignmentStatus.CANCELLED);
        return assignmentRepository.save(assignment);
    }

    /**
     * Get available drivers with scores
     */
    private List<DriverScore> getAvailableDriversWithScores(DispatchRequest request) {
        List<DriverScore> scores = new ArrayList<>();

        // Mock implementation - in production, call fleet-service
        // For now, return mock data
        log.info("Getting available drivers (mock implementation)");

        // In production, this would be:
        // ResponseEntity<List<Driver>> response = restTemplate.exchange(
        // FLEET_SERVICE_URL + "/drivers/available",
        // HttpMethod.GET,
        // null,
        // new ParameterizedTypeReference<List<Driver>>() {}
        // );

        return scores;
    }

    /**
     * Calculate driver score based on multiple factors
     */
    private double calculateDriverScore(DriverScore driver, DispatchRequest request) {
        double score = 100.0;

        // Factor 1: Distance (closer is better)
        // Reduce score by 1 point per km
        score -= driver.getDistanceToPickup();

        // Factor 2: Estimated time (faster is better)
        // Reduce score by 0.5 points per minute
        score -= (driver.getEstimatedTimeToPickup() * 0.5);

        // Factor 3: Vehicle type match (if preference specified)
        if (request.getVehicleTypePreference() != null) {
            if (driver.getVehicleType().equals(request.getVehicleTypePreference())) {
                score += 20.0; // Bonus for matching vehicle type
            }
        }

        return Math.max(0, score); // Ensure non-negative
    }

    /**
     * Update order service with driver assignment
     */
    private void updateOrderAssignment(String orderId, Long driverId, Long vehicleId) {
        // In production, make REST call to order-service
        log.info("Updating order {} with driver {} and vehicle {}", orderId, driverId, vehicleId);

        // Example:
        // Map<String, Object> request = Map.of(
        // "driverId", driverId.toString(),
        // "vehicleId", vehicleId.toString()
        // );
        // restTemplate.postForEntity(
        // ORDER_SERVICE_URL + "/orders/" + orderId + "/assign",
        // request,
        // Void.class
        // );
    }

    /**
     * Update fleet service with driver assignment
     */
    private void updateDriverAssignment(Long driverId, String orderId, Long vehicleId) {
        // In production, make REST call to fleet-service
        log.info("Updating driver {} with order {} and vehicle {}", driverId, orderId, vehicleId);

        // Example:
        // Map<String, Object> request = Map.of(
        // "orderId", orderId,
        // "vehicleId", vehicleId
        // );
        // restTemplate.postForEntity(
        // FLEET_SERVICE_URL + "/drivers/" + driverId + "/assign",
        // request,
        // Void.class
        // );
    }

    /**
     * Initiate dispatch process from Order Created Event
     */
    public void initiateDispatch(com.logistics.platform.common.dto.order.TransportOrderDto orderDto) {
        log.info("Initiating dispatch for order: {}", orderDto.getOrderId());

        try {
            DispatchRequest request = DispatchRequest.builder()
                    .orderId(orderDto.getOrderId())
                    .pickupLatitude(orderDto.getPickupLat())
                    .pickupLongitude(orderDto.getPickupLng())
                    .dropLatitude(orderDto.getDropLat())
                    .dropLongitude(orderDto.getDropLng())
                    .weightKg(orderDto.getWeightKg())
                    .build();

            autoDispatch(request);

        } catch (Exception e) {
            log.error("Error initiating dispatch for order {}: {}", orderDto.getOrderId(), e.getMessage());
        }
    }
}
