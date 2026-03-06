package com.logistics.fleet.service;

import com.logistics.fleet.model.Vehicle;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictiveMaintenanceService {

    // private final MlServiceClient mlServiceClient; // Available in shared-lib

    /**
     * Evaluates a vehicle's telematics and mileage to predict maintenance needs.
     */
    public MaintenancePrediction predictMaintenance(Vehicle vehicle) {
        log.info("Predicting maintenance schedule for Vehicle [{}] at mileage {} km", vehicle.getLicensePlate(),
                vehicle.getMileageKm());

        try {
            // Attempt to use ML service for anomaly detection and TTF (Time-To-Failure)
            // prediction
            // return mlServiceClient.predictVehicleMaintenance(vehicle.getId(),
            // vehicle.getMileageKm(), ...);

            // Fallback to static threshold model
            return calculateHeuristicMaintenance(vehicle);
        } catch (Exception e) {
            log.warn(
                    "ML Predictive Maintenance unavailable for {}. Falling back to standard mileage threshold tracking. Error: {}",
                    vehicle.getLicensePlate(), e.getMessage());
            return calculateHeuristicMaintenance(vehicle);
        }
    }

    private MaintenancePrediction calculateHeuristicMaintenance(Vehicle vehicle) {
        // Simple static threshold intervals
        int maintenanceIntervalKm = 10000;

        int currentMileage = vehicle.getMileageKm() != null ? vehicle.getMileageKm() : 0;
        int nextMaintenanceKm = ((currentMileage / maintenanceIntervalKm) + 1) * maintenanceIntervalKm;
        int kmUntilMaintenance = nextMaintenanceKm - currentMileage;

        // Roughly map km to days (assuming avg 200 km/day)
        int estimatedDaysRemaining = kmUntilMaintenance / 200;

        boolean isCritical = kmUntilMaintenance < 1000; // Less than 1000km to go

        return MaintenancePrediction.builder()
                .predictedMaintenanceDate(LocalDateTime.now().plusDays(estimatedDaysRemaining))
                .remainingKm(kmUntilMaintenance)
                .isCritical(isCritical)
                .recommendation(isCritical ? "Schedule maintenance within 5 days" : "Operating normally")
                .confidenceScore(0.50) // Heuristic confidence is low compared to ML
                .build();
    }

    @Data
    @Builder
    public static class MaintenancePrediction {
        private final LocalDateTime predictedMaintenanceDate;
        private final Integer remainingKm;
        private final Boolean isCritical;
        private final String recommendation;
        private final Double confidenceScore;
    }
}
