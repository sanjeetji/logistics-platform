package com.logistics.fleet.service;

import com.logistics.platform.common.dto.location.LocationDto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProprietaryRoutingService {

    // private final MlServiceClient mlServiceClient; // Available in shared-lib
    // private final MapboxClient mapboxClient;

    /**
     * Calculates the best route between origin and destination.
     * Attempts to use the proprietary ML-driven routing engine first, falling back
     * to Mapbox.
     */
    public RouteResult calculateOptimalRoute(LocationDto origin, LocationDto destination) {
        log.info("Calculating optimal route from [{}] to [{}]", origin.getAddress(), destination.getAddress());

        try {
            // Attempt Proprietary ML Routing (Traffic-aware, Historical data)
            // return mlServiceClient.calculateRoute(origin, destination);
            return simulateProprietaryMLRoute(origin, destination);
        } catch (Exception e) {
            log.warn("Proprietary ML Routing Engine unavailable. Falling back to Mapbox/Google Maps API. Error: {}",
                    e.getMessage());
            return fetchFallbackMapboxRoute(origin, destination);
        }
    }

    private RouteResult simulateProprietaryMLRoute(LocationDto origin, LocationDto destination) {
        log.debug("Using internal ML Route Heuristic.");
        return RouteResult.builder()
                .distanceKm(45.5)
                .estimatedDurationMinutes(65)
                .providerUsed("PROPRIETARY_ML")
                .encodedPolyline("mock_polyline_xyz123")
                .waypoints(List.of(origin, destination))
                .build();
    }

    private RouteResult fetchFallbackMapboxRoute(LocationDto origin, LocationDto destination) {
        log.debug("Using Mapbox standard routing fallback.");
        return RouteResult.builder()
                .distanceKm(46.2)
                .estimatedDurationMinutes(70)
                .providerUsed("MAPBOX_FALLBACK")
                .encodedPolyline("mapbox_polyline_abc987")
                .waypoints(List.of(origin, destination))
                .build();
    }

    @Data
    @Builder
    public static class RouteResult {
        private final Double distanceKm;
        private final Integer estimatedDurationMinutes;
        private final String providerUsed;
        private final String encodedPolyline;
        private final List<LocationDto> waypoints;
    }
}
