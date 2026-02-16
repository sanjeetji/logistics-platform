package com.logistics.routing.traffic;

import com.logistics.routing.dto.TrafficData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Google Maps Distance Matrix API Integration
 * 
 * Fetches real-time traffic data from Google Maps
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.maps.api-key}")
    private String apiKey;

    @Value("${google.maps.distance-matrix.max-elements:100}")
    private int maxElements;

    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    /**
     * Get traffic-aware distance and duration between two points
     */
    @Cacheable(value = "trafficData", key = "#originLat + '_' + #originLon + '_' + #destLat + '_' + #destLon")
    public TrafficData getTrafficData(double originLat, double originLon, double destLat, double destLon) {
        
        log.debug("Fetching traffic data from Google Maps: ({},{}) -> ({},{})", 
            originLat, originLon, destLat, destLon);

        try {
            // Build request URL
            String url = buildDistanceMatrixUrl(originLat, originLon, destLat, destLon);
            
            // Call Google Maps API
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "OK".equals(response.get("status"))) {
                return parseDistanceMatrixResponse(response, originLat, originLon, destLat, destLon);
            } else {
                log.warn("Google Maps API returned non-OK status: {}", response != null ? response.get("status") : "null");
                return createFallbackTrafficData(originLat, originLon, destLat, destLon);
            }
            
        } catch (Exception e) {
            log.error("Error fetching traffic data from Google Maps", e);
            return createFallbackTrafficData(originLat, originLon, destLat, destLon);
        }
    }

    /**
     * Build Google Maps Distance Matrix API URL
     */
    private String buildDistanceMatrixUrl(double originLat, double originLon, double destLat, double destLon) {
        return String.format(
            "%s?origins=%f,%f&destinations=%f,%f&departure_time=now&traffic_model=best_guess&key=%s",
            DISTANCE_MATRIX_URL,
            originLat, originLon,
            destLat, destLon,
            apiKey
        );
    }

    /**
     * Parse Google Maps Distance Matrix API response
     */
    private TrafficData parseDistanceMatrixResponse(Map<String, Object> response, 
                                                    double originLat, double originLon, 
                                                    double destLat, double destLon) {
        try {
            Map<String, Object> row = ((Map<String, Object>[]) response.get("rows"))[0];
            Map<String, Object> element = ((Map<String, Object>[]) row.get("elements"))[0];
            
            if ("OK".equals(element.get("status"))) {
                Map<String, Object> distance = (Map<String, Object>) element.get("distance");
                Map<String, Object> duration = (Map<String, Object>) element.get("duration");
                Map<String, Object> durationInTraffic = (Map<String, Object>) element.get("duration_in_traffic");
                
                long distanceMeters = ((Number) distance.get("value")).longValue();
                long durationSeconds = ((Number) duration.get("value")).longValue();
                long durationInTrafficSeconds = durationInTraffic != null 
                    ? ((Number) durationInTraffic.get("value")).longValue() 
                    : durationSeconds;
                
                TrafficData trafficData = TrafficData.builder()
                    .originLat(originLat)
                    .originLon(originLon)
                    .destinationLat(destLat)
                    .destinationLon(destLon)
                    .distanceMeters(distanceMeters)
                    .durationSeconds(durationSeconds)
                    .durationInTrafficSeconds(durationInTrafficSeconds)
                    .timestamp(System.currentTimeMillis())
                    .source("GOOGLE_MAPS")
                    .build();
                
                trafficData.setTrafficDelayPercent(trafficData.calculateTrafficDelay());
                trafficData.setTrafficLevel(trafficData.determineTrafficLevel());
                
                log.debug("Traffic data: distance={}m, duration={}s, traffic={}s, delay={}%", 
                    distanceMeters, durationSeconds, durationInTrafficSeconds, 
                    trafficData.getTrafficDelayPercent());
                
                return trafficData;
            }
        } catch (Exception e) {
            log.error("Error parsing Google Maps response", e);
        }
        
        return createFallbackTrafficData(originLat, originLon, destLat, destLon);
    }

    /**
     * Create fallback traffic data using Haversine distance
     */
    private TrafficData createFallbackTrafficData(double originLat, double originLon, 
                                                  double destLat, double destLon) {
        
        double distanceKm = calculateHaversineDistance(originLat, originLon, destLat, destLon);
        long distanceMeters = (long) (distanceKm * 1000);
        long durationSeconds = (long) ((distanceKm / 40.0) * 3600); // Assume 40 km/h average speed
        
        return TrafficData.builder()
            .originLat(originLat)
            .originLon(originLon)
            .destinationLat(destLat)
            .destinationLon(destLon)
            .distanceMeters(distanceMeters)
            .durationSeconds(durationSeconds)
            .durationInTrafficSeconds(durationSeconds)
            .trafficLevel(TrafficData.TrafficLevel.LIGHT)
            .trafficDelayPercent(0.0)
            .timestamp(System.currentTimeMillis())
            .source("FALLBACK")
            .build();
    }

    /**
     * Calculate Haversine distance between two coordinates
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
}
