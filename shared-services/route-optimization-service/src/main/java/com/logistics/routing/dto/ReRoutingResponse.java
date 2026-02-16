package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Re-routing Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReRoutingResponse {

    private String reRoutingId;
    private String routeId;
    private ReRoutingStatus status;
    
    // New route
    private List<RouteOptimizationResponse.RouteStop> newStops;
    private Integer totalStops;
    private Double totalDistanceKm;
    private Long totalDurationSeconds;
    
    // Impact analysis
    private Integer stopsAdded;
    private Integer stopsRemoved;
    private Integer stopsReordered;
    private Long delaySeconds;
    private Double additionalCostEstimate;
    
    // Notification
    private Boolean driverNotified;
    private String notificationMessage;
    
    private LocalDateTime reRoutedAt;
    private Long computationTimeMs;

    public enum ReRoutingStatus {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILED,
        NO_CHANGE_NEEDED
    }
}
