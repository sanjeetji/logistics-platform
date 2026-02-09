package com.logistics.analytics.service;

import com.logistics.analytics.model.CarbonFootprint;
import com.logistics.analytics.repository.CarbonFootprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmissionService {

    private final CarbonFootprintRepository carbonFootprintRepository;

    // Emission factors (kg CO2 per km)
    private static final double EMISSION_FACTOR_BIKE = 0.0;
    private static final double EMISSION_FACTOR_SCOOTER = 0.05;
    private static final double EMISSION_FACTOR_CAR = 0.12;
    private static final double EMISSION_FACTOR_VAN = 0.18;
    private static final double EMISSION_FACTOR_TRUCK = 0.85;

    public CarbonFootprint calculateAndSaveEmission(String entityId, String entityType, double distanceKm,
            String vehicleType) {
        double factor = getEmissionFactor(vehicleType);
        double totalEmission = distanceKm * factor;

        CarbonFootprint footprint = CarbonFootprint.builder()
                .entityId(entityId)
                .entityType(entityType)
                .distanceKm(distanceKm)
                .vehicleType(vehicleType)
                .emissionFactor(factor)
                .totalCo2EmissionKg(totalEmission)
                .calculatedAt(LocalDateTime.now())
                .build();

        return carbonFootprintRepository.save(footprint);
    }

    private double getEmissionFactor(String vehicleType) {
        if (vehicleType == null)
            return EMISSION_FACTOR_VAN; // Default
        switch (vehicleType.toUpperCase()) {
            case "BIKE":
                return EMISSION_FACTOR_BIKE;
            case "SCOOTER":
                return EMISSION_FACTOR_SCOOTER;
            case "CAR":
                return EMISSION_FACTOR_CAR;
            case "VAN":
                return EMISSION_FACTOR_VAN;
            case "TRUCK":
                return EMISSION_FACTOR_TRUCK;
            default:
                return EMISSION_FACTOR_VAN;
        }
    }

    public Double getTotalEmissions() {
        Double total = carbonFootprintRepository.getTotalEmissions();
        return total != null ? total : 0.0;
    }
}
