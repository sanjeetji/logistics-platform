package com.logistics.routing.kafka;

import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.dto.ReRoutingResponse;
import com.logistics.routing.rerouting.DynamicReRoutingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer for Re-routing Trigger Events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReRoutingTriggerConsumer {

    private final DynamicReRoutingService reRoutingService;

    /**
     * Listen for re-routing trigger events
     */
    @KafkaListener(topics = "rerouting-triggers", groupId = "route-optimization-service")
    public void handleReRoutingTrigger(ReRoutingTriggerEvent event) {
        log.info("Received re-routing trigger: route={}, trigger={}, driver={}", 
            event.getRouteId(), event.getTrigger(), event.getDriverId());

        try {
            // Build re-routing request from event
            ReRoutingRequest request = ReRoutingRequest.builder()
                .routeId(event.getRouteId())
                .vehicleId(event.getVehicleId())
                .driverId(event.getDriverId())
                .trigger(event.getTrigger())
                .triggerDescription(event.getDescription())
                .currentLatitude(event.getLatitude())
                .currentLongitude(event.getLongitude())
                .urgentOrderId(event.getUrgentOrderId())
                .failedStopId(event.getFailedStopId())
                .breakDurationMinutes(event.getBreakDurationMinutes())
                .weatherAlertZone(event.getWeatherAlertZone())
                .preserveTimeWindows(true)
                .minimizeDelay(true)
                .build();
            
            // Trigger re-routing
            ReRoutingResponse response = reRoutingService.triggerReRouting(request);
            
            log.info("Re-routing completed: status={}, stops={}", 
                response.getStatus(), response.getTotalStops());
            
        } catch (Exception e) {
            log.error("Error processing re-routing trigger event", e);
        }
    }
}
