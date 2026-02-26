package com.logistics.fleet.service;

import com.logistics.fleet.model.Vehicle;
import com.logistics.fleet.model.VehicleType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class SustainabilityService {

    // private final MlServiceClient mlServiceClient; // Usually from shared-lib

    /**
     * Calculates the estimated carbon emissions for a given trip.
     * Integrates with ML Service for predictive dynamic routing impact or falls
     * back to standard heuristics.
     */
    public CarbonEmissionResult calculateTripEmissions(Vehicle vehicle, double distanceKm, double payloadWeightKg) {
        log.info("Calculating carbon emissions for Vehicle [{}] Type [{}] over {} km with {} kg payload",
                vehicle.getLicensePlate(), vehicle.getType(), distanceKm, payloadWeightKg);

        try {
            // ML Integration point:
            // return mlServiceClient.predictCarbonEmission(vehicle.getType(), distanceKm,
            // payloadWeightKg);
            // Fallthrough to heuristic
            return applyHeuristicCalculation(vehicle, distanceKm, payloadWeightKg);
        } catch (Exception e) {
            log.warn("Failed to reach ML Service for Emission calculation. Using heuristic fallback. Error: {}",
                    e.getMessage());
            return applyHeuristicCalculation(vehicle, distanceKm, payloadWeightKg);
        }
    }

    private CarbonEmissionResult applyHeuristicCalculation(Vehicle vehicle, double distanceKm, double payloadWeightKg) {
        // Base emission factors (grams of CO2 per km)
        double baseEmissionGramsPerKm;

        VehicleType type = vehicle.getType() != null ? vehicle.getType() : VehicleType.TRUCK;

        switch (type) {
            case BICYCLE:
            case WALKING:
                baseEmissionGramsPerKm = 0.0;
                break;
            case CAR:
                baseEmissionGramsPerKm = 120.0;
                break;
            case VAN:
                baseEmissionGramsPerKm = 210.0;
                break;
            case TRUCK:
                baseEmissionGramsPerKm = 850.0; // Heavy duty standard
                // Weight penalty for trucks (approx 0.05 grams per kg extra per km)
                baseEmissionGramsPerKm += (payloadWeightKg * 0.05);
                break;
            default:
                baseEmissionGramsPerKm = 150.0;
        }

        double totalEmissionsGrams = baseEmissionGramsPerKm * distanceKm;
        double totalEmissionsKg = totalEmissionsGrams / 1000.0;

        BigDecimal roundedEmissions = new BigDecimal(totalEmissionsKg).setScale(2, RoundingMode.HALF_UP);

        return CarbonEmissionResult.builder()
                .totalEmissionKg(roundedEmissions)
                .distanceKm(new BigDecimal(distanceKm).setScale(2, RoundingMode.HALF_UP))
                .vehicleType(type.name())
                .calculationMethod("HEURISTIC_FALLBACK")
                .build();
    }

    @Data
    @Builder
    public static class CarbonEmissionResult {
        private final BigDecimal totalEmissionKg;
        private final BigDecimal distanceKm;
        private final String vehicleType;
        private final String calculationMethod;
    }
}
