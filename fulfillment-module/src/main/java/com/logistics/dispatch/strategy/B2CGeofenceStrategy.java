package com.logistics.dispatch.strategy;

import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.clients.geo.GeoServiceClient;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("B2C_ON_DEMAND")
@RequiredArgsConstructor
public class B2CGeofenceStrategy implements DispatchStrategy {

    private final GeoServiceClient geoServiceClient;

    @Override
    public boolean dispatch(TransportOrderDto order, DispatchJob job) {
        log.info("Executing B2C Geofence Search for Order: {}", order.getOrderId());

        try {
            ApiResponse<List<DriverLocationDto>> response = geoServiceClient.findDriversNearby(
                    order.getPickupLat(),
                    order.getPickupLng(),
                    5.0); // 5km radius

            if (response.getData() != null && !response.getData().isEmpty()) {
                DriverLocationDto bestDriver = response.getData().get(0);
                job.setMatchedDriverId(bestDriver.getDriverId());
                log.info("Found driver {} for order {}", bestDriver.getDriverId(), order.getOrderId());
                return true;
            } else {
                log.warn("No drivers found within 5km for order {}", order.getOrderId());
                job.setLastErrorMessage("No drivers found in 5km radius");
                return false;
            }
        } catch (Exception e) {
            log.error("Error calling Geo Service: {}", e.getMessage());
            job.setLastErrorMessage("Geo Service Error: " + e.getMessage());
            return false;
        }
    }
}
