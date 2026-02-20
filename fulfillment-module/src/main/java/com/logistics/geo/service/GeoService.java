package com.logistics.geo.service;

import com.logistics.geo.dto.DistanceRequest;
import com.logistics.geo.dto.DistanceResponse;
import com.logistics.geo.dto.GeoCoordinates;
import org.springframework.stereotype.Service;

@Service
public class GeoService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
    private static final String OSRM_API_URL = "http://router.project-osrm.org/route/v1/driving/";

    public DistanceResponse calculateDistance(DistanceRequest request) {
        try {
            return calculateDistanceOsrm(request);
        } catch (Exception e) {
            System.err.println("OSRM failed, falling back to Haversine: " + e.getMessage());
            return calculateDistanceHaversine(request);
        }
    }

    private DistanceResponse calculateDistanceOsrm(DistanceRequest request) {
        String coordinates = String.format("%f,%f;%f,%f",
                request.getOrigin().getLongitude(), request.getOrigin().getLatitude(),
                request.getDestination().getLongitude(), request.getDestination().getLatitude());

        String url = OSRM_API_URL + coordinates + "?overview=false";

        // Simple JSON parsing
        var response = restTemplate.getForObject(url, String.class);

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response);

            if (root.path("code").asText().equals("Ok")) {
                double distanceMeters = root.path("routes").get(0).path("distance").asDouble();
                double durationSeconds = root.path("routes").get(0).path("duration").asDouble();

                return DistanceResponse.builder()
                        .distanceKm(distanceMeters / 1000.0)
                        .durationMinutes(durationSeconds / 60.0)
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OSRM response", e);
        }
        throw new RuntimeException("No route found");
    }

    private DistanceResponse calculateDistanceHaversine(DistanceRequest request) {
        double lat1 = request.getOrigin().getLatitude();
        double lon1 = request.getOrigin().getLongitude();
        double lat2 = request.getDestination().getLatitude();
        double lon2 = request.getDestination().getLongitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS_KM * c;

        // Mock duration: assume 40 km/h average speed in city
        double durationMinutes = (distanceKm / 40.0) * 60;

        return DistanceResponse.builder()
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                .durationMinutes(Math.round(durationMinutes * 100.0) / 100.0)
                .build();
    }

    public GeoCoordinates geocode(String address) {
        // Mock implementation
        // In real world, call Google Maps Geocoding API
        return GeoCoordinates.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();
    }
}
