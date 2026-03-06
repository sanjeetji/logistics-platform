package com.logistics.routing.matching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Driver Skills
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverSkills {

    private String driverId;
    private String driverName;
    
    // Skills
    private List<String> certifications; // e.g., "HAZMAT", "REFRIGERATED", "FRAGILE"
    private List<String> vehicleTypes; // e.g., "VAN", "TRUCK", "MOTORCYCLE"
    private List<String> languages;
    
    // Experience
    private Integer yearsExperience;
    private Double averageRating;
    private Integer totalDeliveries;
    
    // Preferences
    private List<String> preferredZones;
    private Boolean availableForUrgent;
    private Integer maxStopsPerRoute;
}
