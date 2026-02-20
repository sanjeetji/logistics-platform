package com.logistics.routing.zone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Delivery Zone
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryZone {

    private String zoneId;
    private String zoneName;
    private ZoneType zoneType;
    
    // Geographic bounds
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radiusKm;
    
    // Zone characteristics
    private Integer priority; // 1-10, higher = more priority
    private Double trafficMultiplier; // 1.0 = normal
    private Boolean requiresSpecialVehicle;
    private String preferredVehicleType;

    public enum ZoneType {
        URBAN,
        SUBURBAN,
        RURAL,
        INDUSTRIAL,
        RESIDENTIAL,
        COMMERCIAL
    }
}
