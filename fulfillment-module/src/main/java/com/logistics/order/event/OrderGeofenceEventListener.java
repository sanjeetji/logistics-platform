package com.logistics.order.event;

import com.logistics.order.statemachine.OrderEvent;
import com.logistics.order.statemachine.OrderStateMachineService;
import com.logistics.platform.event.dto.GeofenceEvent;
import com.logistics.platform.event.dto.GeofenceEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderGeofenceEventListener {

    private final OrderStateMachineService stateMachineService;

    @KafkaListener(topics = "geofence-events", groupId = "order-service-geofence")
    public void handleGeofenceEvent(GeofenceEvent event) {
        if (!"ORDER".equalsIgnoreCase(event.getAssociatedEntityType())) {
            return;
        }

        log.info("Received geofence event for order {}: {} - Purpose: {}",
                event.getAssociatedEntityId(), event.getEventType(), event.getPurpose());

        String orderId = event.getAssociatedEntityId();

        try {
            if (event.getEventType() == GeofenceEventType.ENTER) {
                handleEntry(orderId, event);
            } else {
                handleExit(orderId, event);
            }
        } catch (Exception e) {
            log.error("Error handling geofence event for order {}", orderId, e);
        }
    }

    private void handleEntry(String orderId, GeofenceEvent event) {
        switch (event.getPurpose()) {
            case PICKUP:
                log.info("Driver entered PICKUP zone for order {}. Triggering PICKUP", orderId);
                stateMachineService.transitionOrder(orderId, OrderEvent.PICKUP,
                        "Automated geofence entry (Pickup Zone)");
                break;
            case DELIVERY:
                log.info("Driver entered DELIVERY zone for order {}. Triggering DELIVER", orderId);
                stateMachineService.transitionOrder(orderId, OrderEvent.DELIVER,
                        "Automated geofence entry (Delivery Zone)");
                break;
            default:
                log.debug("No entry transition for purpose: {}", event.getPurpose());
        }
    }

    private void handleExit(String orderId, GeofenceEvent event) {
        switch (event.getPurpose()) {
            case PICKUP:
                log.info("Driver exited PICKUP zone for order {}. Triggering START_TRANSIT", orderId);
                stateMachineService.transitionOrder(orderId, OrderEvent.START_TRANSIT,
                        "Automated geofence exit (Pickup Zone)");
                break;
            default:
                log.debug("No exit transition for purpose: {}", event.getPurpose());
        }
    }
}
