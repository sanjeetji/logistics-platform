package com.logistics.dispatch.strategy;

import com.logistics.platform.api.fleet.FleetClient;
import com.logistics.platform.common.dto.fleet.DriverDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("b2cGeofenceStrategy")
@RequiredArgsConstructor
public class B2CGeofenceStrategy implements DispatchStrategy {

    private final FleetClient fleetClient;

    @Override
    public void dispatchOrder(Long orderId) {
        log.info("Executing B2C Geofence Dispatch for Order ID: {}", orderId);
        
        // Mocking radius search by getting all drivers (in real scenario, GeoService would filter)
        List<DriverDto> availableDrivers = fleetClient.getAllDrivers(); // This requires Feign Client
        
        log.info("Found {} drivers. Broadcasting order...", availableDrivers.size());
        
        // Logic to send notification to these drivers
    }
}
