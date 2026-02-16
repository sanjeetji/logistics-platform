package com.logistics.pricing.service;

public interface ServiceabilityService {

    /**
     * Check if a location is within a serviceable zone
     */
    boolean isServiceable(double latitude, double longitude);

    /**
     * Check if a route (pickup and drop) is serviceable
     */
    boolean isRouteServiceable(double pickupLat, double pickupLon, double dropLat, double dropLon);

    /**
     * Find the serviceable zone for a location
     */
    java.util.Optional<com.logistics.pricing.model.ServiceableZone> findServiceableZone(double latitude,
            double longitude);
}
