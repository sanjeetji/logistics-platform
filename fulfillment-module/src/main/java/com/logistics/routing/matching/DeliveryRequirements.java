package com.logistics.routing.matching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Delivery Requirements
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryRequirements {

    private String stopId;
    
    // Required skills
    private List<String> requiredCertifications;
    private String requiredVehicleType;
    private List<String> requiredLanguages;
    
    // Preferences
    private Integer minimumExperience;
    private Double minimumRating;
    private Boolean isUrgent;
    private String preferredZone;
}
