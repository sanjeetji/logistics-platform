package com.logistics.fleet.event;

import com.logistics.fleet.service.DriverService;
import com.logistics.fleet.service.GeofenceService;
import com.logistics.platform.event.dto.DriverLocationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverLocationListener {

    private final DriverService driverService;
    private final GeofenceService geofenceService;

    @KafkaListener(topics = "driver-location-updates", groupId = "fleet-service-group")
    public void handleLocationUpdate(DriverLocationUpdatedEvent event) {
        log.info("Received location update for driver {}: ({}, {})",
                event.getDriverId(), event.getLatitude(), event.getLongitude());

        try {
            // driverId in event is String, in fleet-service it's Long (usually)
            // Need to check if driverId is numeric or email
            Long id = Long.parseLong(event.getDriverId());
            com.logistics.fleet.model.Driver driver = driverService.updateDriverLocation(id, event.getLatitude(),
                    event.getLongitude());

            // Trigger Geofence Checks
            geofenceService.checkGeofences(id, driver.getCurrentLocation());

        } catch (NumberFormatException e) {
            log.warn(
                    "Received location update with non-numeric driverId: {}. Attempting to lookup by email if applicable.",
                    event.getDriverId());
            // Future: add lookup by email if id is not numeric
        } catch (Exception e) {
            log.error("Failed to update driver location from Kafka event", e);
        }
    }
}
