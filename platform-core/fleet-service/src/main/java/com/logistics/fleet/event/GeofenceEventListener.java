package com.logistics.fleet.event;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.repository.DriverRepository;
import com.logistics.fleet.statemachine.DriverEvent;
import com.logistics.fleet.statemachine.DriverStateMachineService;
import com.logistics.platform.event.dto.GeofenceEvent;
import com.logistics.platform.event.dto.GeofenceEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeofenceEventListener {

    private final DriverStateMachineService stateMachineService;
    private final DriverRepository driverRepository;

    @KafkaListener(topics = "geofence-events", groupId = "fleet-service-geofence")
    public void handleGeofenceEvent(GeofenceEvent event) {
        log.info("Received geofence event: {} for driver {}", event.getEventType(), event.getDriverId());

        try {
            Long driverId = Long.parseLong(event.getDriverId());
            Driver driver = driverRepository.findById(driverId).orElse(null);

            if (driver == null) {
                log.warn("Driver not found for ID: {}", driverId);
                return;
            }

            if (event.getEventType() == GeofenceEventType.ENTER) {
                handleEntry(driver, event);
            } else {
                handleExit(driver, event);
            }
        } catch (Exception e) {
            log.error("Error handling geofence event", e);
        }
    }

    private void handleEntry(Driver driver, GeofenceEvent event) {
        switch (event.getPurpose()) {
            case PICKUP:
                log.info("Driver {} entered PICKUP zone. Transitioning to AT_PICKUP", driver.getEmail());
                stateMachineService.transitionDriver(driver.getEmail(), DriverEvent.ARRIVE_PICKUP,
                        "Automated geofence entry");
                break;
            case DELIVERY:
                log.info("Driver {} entered DELIVERY zone. Transitioning to AT_DELIVERY", driver.getEmail());
                stateMachineService.transitionDriver(driver.getEmail(), DriverEvent.ARRIVE_DELIVERY,
                        "Automated geofence entry");
                break;
            default:
                log.debug("No entry transition for purpose: {}", event.getPurpose());
        }
    }

    private void handleExit(Driver driver, GeofenceEvent event) {
        switch (event.getPurpose()) {
            case PICKUP:
                log.info("Driver {} exited PICKUP zone. Transitioning to START_DELIVERY", driver.getEmail());
                stateMachineService.transitionDriver(driver.getEmail(), DriverEvent.START_DELIVERY,
                        "Automated geofence exit");
                break;
            default:
                log.debug("No exit transition for purpose: {}", event.getPurpose());
        }
    }
}
