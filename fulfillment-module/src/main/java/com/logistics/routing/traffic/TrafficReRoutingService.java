package com.logistics.routing.traffic;

import com.logistics.order.model.Order;
import com.logistics.order.repository.OrderRepository;
import com.logistics.routing.dto.ReRoutingRequest;
import com.logistics.routing.kafka.TrafficUpdateEvent;
import com.logistics.routing.rerouting.DynamicReRoutingService;
import com.logistics.routing.rerouting.ReRoutingTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Traffic Re-Routing Service
 * 
 * Orchestrates re-routing for all vehicles affected by a traffic incident
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrafficReRoutingService {

    private final OrderRepository orderRepository;
    private final DynamicReRoutingService dynamicReRoutingService;

    /**
     * Handle traffic update and trigger re-routing for affected active routes
     */
    @Transactional
    public void processTrafficIncident(TrafficUpdateEvent event) {
        log.info("Processing traffic incident for possible re-routing: {}({}, {}) r={}km",
                event.getEventType(), event.getLatitude(), event.getLongitude(), event.getRadiusKm());

        // Find active orders within affected radius
        List<Order> affectedOrders = orderRepository.findAffectedOrders(
                event.getLatitude(), event.getLongitude(), event.getRadiusKm());

        if (affectedOrders.isEmpty()) {
            log.info("No active orders affected by traffic incident: {}", event.getEventId());
            return;
        }

        log.info("Found {} orders affected by traffic incident. Triggering re-optimization...", affectedOrders.size());

        for (Order order : affectedOrders) {
            try {
                triggerReRoutingForOrder(order, event);
            } catch (Exception e) {
                log.error("Failed to trigger re-routing for order {}: {}", order.getOrderId(), e.getMessage());
            }
        }
    }

    private void triggerReRoutingForOrder(Order order, TrafficUpdateEvent event) {
        // Collect remaining stop IDs
        List<String> remainingStopIds = order.getStops().stream()
                .filter(s -> !s.getCompleted())
                .map(s -> String.valueOf(s.getId()))
                .collect(Collectors.toList());

        if (remainingStopIds.isEmpty()) {
            return;
        }

        // Build Re-routing Request
        ReRoutingRequest request = ReRoutingRequest.builder()
                .routeId("R-" + order.getOrderId())
                .vehicleId(order.getVehicleId())
                .driverId(order.getDriverId())
                .trigger(ReRoutingTrigger.TRAFFIC_INCIDENT)
                .triggerDescription(event.getDescription() != null ? event.getDescription()
                        : "Traffic delay: " + event.getEventType())
                .remainingStopIds(remainingStopIds)
                // Use order's pickup location or driver's last known location as current
                // In a mature system, we'd fetch real-time driver coordinates from Redis
                .currentLatitude(order.getPickupLocation().getLatitude())
                .currentLongitude(order.getPickupLocation().getLongitude())
                .preserveTimeWindows(true)
                .minimizeDelay(true)
                .build();

        log.info("Triggering dynamic re-optimization for order {} (Driver: {})", order.getOrderId(),
                order.getDriverId());
        dynamicReRoutingService.triggerReRouting(request);
    }
}
