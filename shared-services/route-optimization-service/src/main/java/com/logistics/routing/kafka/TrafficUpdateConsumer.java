package com.logistics.routing.kafka;

import com.logistics.routing.traffic.TrafficIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer for real-time traffic updates
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrafficUpdateConsumer {

    private final TrafficIntegrationService trafficIntegrationService;

    /**
     * Listen for traffic update events
     */
    @KafkaListener(topics = "traffic-updates", groupId = "route-optimization-service")
    public void handleTrafficUpdate(TrafficUpdateEvent event) {
        log.info("Received traffic update: type={}, severity={}, location=({},{}), radius={}km",
            event.getEventType(), 
            event.getSeverity(),
            event.getLatitude(),
            event.getLongitude(),
            event.getRadiusKm());

        try {
            // Invalidate affected cache entries
            // In a real implementation, this would invalidate all routes within the radius
            trafficIntegrationService.clearAllCache();
            
            log.info("Traffic cache invalidated due to: {}", event.getEventType());
            
        } catch (Exception e) {
            log.error("Error processing traffic update event", e);
        }
    }
}
