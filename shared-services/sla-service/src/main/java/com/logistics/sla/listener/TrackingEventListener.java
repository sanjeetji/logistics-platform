package com.logistics.sla.listener;

import com.logistics.sla.service.SLAPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrackingEventListener {

    private final SLAPredictionService predictionService;

    /**
     * Listen for tracking location updates and predict SLA breaches
     * Expected event structure:
     * {
     * "orderId": "...",
     * "driverId": "...",
     * "currentLat": 12.34,
     * "currentLng": 56.78,
     * "destinationLat": 12.45,
     * "destinationLng": 56.89,
     * "estimatedTimeMinutes": 30
     * }
     */
    @KafkaListener(topics = "tracking.location.updated", groupId = "sla-prediction-group")
    public void handleLocationUpdate(Map<String, Object> event) {
        try {
            String orderId = (String) event.get("orderId");
            Double currentLat = getDoubleValue(event.get("currentLat"));
            Double currentLng = getDoubleValue(event.get("currentLng"));
            Double destinationLat = getDoubleValue(event.get("destinationLat"));
            Double destinationLng = getDoubleValue(event.get("destinationLng"));
            Integer estimatedTimeMinutes = getIntegerValue(event.get("estimatedTimeMinutes"));

            if (orderId != null && currentLat != null && currentLng != null
                    && destinationLat != null && destinationLng != null
                    && estimatedTimeMinutes != null) {

                log.debug("Processing tracking update for order: {}", orderId);
                predictionService.predictBreach(
                        orderId,
                        "ORDER",
                        currentLat,
                        currentLng,
                        destinationLat,
                        destinationLng,
                        estimatedTimeMinutes);
            } else {
                log.warn("Incomplete tracking data received: {}", event);
            }
        } catch (Exception e) {
            log.error("Error processing tracking event for SLA prediction", e);
        }
    }

    private Double getDoubleValue(Object value) {
        if (value == null)
            return null;
        if (value instanceof Double)
            return (Double) value;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntegerValue(Object value) {
        if (value == null)
            return null;
        if (value instanceof Integer)
            return (Integer) value;
        if (value instanceof Number)
            return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
