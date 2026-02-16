package com.logistics.dispatch.engine.impl;

import com.logistics.dispatch.engine.ScoringRule;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.springframework.stereotype.Component;

@Component
public class DistanceScoringRule implements ScoringRule {

    @Override
    public double calculateScore(TransportOrderDto order, DriverLocationDto driver) {
        // Calculate distance between pickup and driver location
        double distance = calculateDistance(
                order.getPickupLat(), order.getPickupLng(),
                driver.getLat(), driver.getLng());

        // Score: 100 - (distance in km * 2)
        // Max distance 50km -> score 0
        return Math.max(0, 100 - (distance * 2));
    }

    @Override
    public int getWeight() {
        return 10; // High importance
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
