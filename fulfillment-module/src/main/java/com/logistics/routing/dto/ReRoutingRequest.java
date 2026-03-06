package com.logistics.routing.dto;

import com.logistics.routing.rerouting.ReRoutingTrigger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Re-routing Request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReRoutingRequest {

    private String routeId;
    private String vehicleId;
    private String driverId;
    private ReRoutingTrigger trigger;
    private String triggerDescription;
    
    // Current route state
    private List<String> remainingStopIds;
    private Double currentLatitude;
    private Double currentLongitude;
    
    // Trigger-specific data
    private String urgentOrderId; // For NEW_URGENT_ORDER
    private String failedStopId;  // For DELIVERY_FAILURE
    private Integer breakDurationMinutes; // For DRIVER_BREAK
    private String weatherAlertZone; // For WEATHER_ALERT
    
    // Constraints
    private Boolean preserveTimeWindows;
    private Boolean minimizeDelay;
    private Integer maxAdditionalStops;
}
