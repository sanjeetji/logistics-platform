package com.logistics.routing.kafka;

import com.logistics.routing.rerouting.ReRoutingTrigger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Re-routing Trigger Event from Kafka
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReRoutingTriggerEvent {

    private String eventId;
    private String routeId;
    private String vehicleId;
    private String driverId;
    private ReRoutingTrigger trigger;
    private String description;
    
    // Location context
    private Double latitude;
    private Double longitude;
    
    // Trigger-specific data
    private String urgentOrderId;
    private String failedStopId;
    private Integer breakDurationMinutes;
    private String weatherAlertZone;
    private String trafficIncidentId;
    
    private Long timestamp;
}
