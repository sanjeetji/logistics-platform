package com.logistics.routing.zone;

import com.logistics.routing.dto.RouteOptimizationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Zone-Based Routing Service
 * 
 * Groups deliveries by geographic zones for efficient routing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneBasedRoutingService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Group stops by delivery zones
     */
    public Map<String, List<RouteOptimizationRequest.DeliveryStop>> groupStopsByZone(
            List<RouteOptimizationRequest.DeliveryStop> stops,
            List<DeliveryZone> zones) {
        
        log.info("Grouping {} stops into {} zones", stops.size(), zones.size());
        
        Map<String, List<RouteOptimizationRequest.DeliveryStop>> zoneGroups = new HashMap<>();
        
        for (RouteOptimizationRequest.DeliveryStop stop : stops) {
            DeliveryZone assignedZone = findNearestZone(stop, zones);
            
            if (assignedZone != null) {
                zoneGroups.computeIfAbsent(assignedZone.getZoneId(), k -> new ArrayList<>())
                         .add(stop);
            } else {
                // Unassigned zone
                zoneGroups.computeIfAbsent("UNASSIGNED", k -> new ArrayList<>())
                         .add(stop);
            }
        }
        
        log.info("Stops grouped into {} zones", zoneGroups.size());
        return zoneGroups;
    }

    /**
     * Find nearest zone for a stop
     */
    private DeliveryZone findNearestZone(RouteOptimizationRequest.DeliveryStop stop, 
                                        List<DeliveryZone> zones) {
        DeliveryZone nearestZone = null;
        double minDistance = Double.MAX_VALUE;
        
        for (DeliveryZone zone : zones) {
            double distance = calculateDistance(
                stop.getLatitude(), stop.getLongitude(),
                zone.getCenterLatitude(), zone.getCenterLongitude()
            );
            
            if (distance <= zone.getRadiusKm() && distance < minDistance) {
                minDistance = distance;
                nearestZone = zone;
            }
        }
        
        return nearestZone;
    }

    /**
     * Prioritize zones for routing
     */
    public List<DeliveryZone> prioritizeZones(List<DeliveryZone> zones) {
        return zones.stream()
            .sorted(Comparator.comparingInt(DeliveryZone::getPriority).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Calculate distance between two points
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
}
