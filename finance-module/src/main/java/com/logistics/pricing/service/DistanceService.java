package com.logistics.pricing.service;

import com.logistics.pricing.client.MLEtaClient; // Assuming MLEtaClient is in this package or needs to be imported

import org.springframework.stereotype.Service;

/**
 * Interface for distance and time estimation
 */
@Service
public class DistanceService {

    private final MLEtaClient mlEtaClient;

    public DistanceService(org.springframework.beans.factory.ObjectProvider<MLEtaClient> mlEtaClientProvider) {
        // Use ObjectProvider to make the client optional or handle circular deps if any
        this.mlEtaClient = mlEtaClientProvider.getIfAvailable();
    }

    /**
     * Calculate distance between two points
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        int R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Estimate time to reach destination based on distance
     */
    public int estimateTime(double distanceKm) {
        // Fallback to average speed of 40 km/h if no ML client or specific coordinates
        return (int) ((distanceKm / 40.0) * 60);
    }

    public int estimateTime(double pickupLat, double pickupLon, double dropLat, double dropLon, String vehicleType) {
        try {
            if (mlEtaClient != null) {
                MLEtaClient.ETARequest request = MLEtaClient.ETARequest.builder()
                        .pickupLatitude(pickupLat)
                        .pickupLongitude(pickupLon)
                        .dropLatitude(dropLat)
                        .dropLongitude(dropLon)
                        .vehicleType(vehicleType != null ? vehicleType : "CAR")
                        .trafficCondition("MEDIUM") // Could be dynamic based on time
                        .build();

                MLEtaClient.ETAResponse response = mlEtaClient.predictEta(request);
                if (response != null && response.getEstimatedMinutes() != null) {
                    return response.getEstimatedMinutes();
                }
            }
        } catch (Exception e) {
            // Log error and fallback
            System.err.println("ML ETA Service unavailable: " + e.getMessage());
        }

        // Fallback calculation
        double distance = calculateDistance(pickupLat, pickupLon, dropLat, dropLon);
        return estimateTime(distance);
    }
}
