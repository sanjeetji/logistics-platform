package com.logistics.route.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GreenRoutingService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GreenRoutingService.class);

    // CO2 Emission Factors (kg per km)
    private static final double FACTOR_TRUCK_DIESEL = 0.95;
    private static final double FACTOR_VAN_PETROL = 0.25;
    private static final double FACTOR_BIKE_PETROL = 0.08;
    private static final double FACTOR_EV = 0.0;

    /**
     * Calculate CO2 emissions for a given trip.
     * 
     * @param distanceKm Distance in Kilometers
     * @param vehicleType Type of vehicle (TRUCK, VAN, BIKE, EV)
     * @return CO2 emissions in KG
     */
    public BigDecimal calculateCO2Emission(double distanceKm, String vehicleType) {
        double factor = getEmissionFactor(vehicleType);
        double emissions = distanceKm * factor;
        
        log.debug("Calculated CO2 for {}km using {}: {} kg", distanceKm, vehicleType, emissions);
        
        return BigDecimal.valueOf(emissions).setScale(2, RoundingMode.HALF_UP);
    }

    private double getEmissionFactor(String vehicleType) {
        if (vehicleType == null) return FACTOR_VAN_PETROL; // Default
        
        return switch (vehicleType.toUpperCase()) {
            case "TRUCK" -> FACTOR_TRUCK_DIESEL;
            case "VAN" -> FACTOR_VAN_PETROL;
            case "BIKE", "SCOOTER" -> FACTOR_BIKE_PETROL;
            case "EV", "ELECTRIC_SCOOTER", "ELECTRIC_VAN" -> FACTOR_EV;
            default -> FACTOR_VAN_PETROL;
        };
    }
}
