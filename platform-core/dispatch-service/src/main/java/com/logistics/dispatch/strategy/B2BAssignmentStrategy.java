package com.logistics.dispatch.strategy;

import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import com.logistics.platform.common.dto.fleet.DriverDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("B2B_SHIPMENT")
@RequiredArgsConstructor
public class B2BAssignmentStrategy implements DispatchStrategy {

    private final com.logistics.platform.api.fleet.FleetClient fleetClient;

    @Override
    public boolean dispatch(TransportOrderDto order, DispatchJob job) {
        log.info("Executing B2B Dedicated Fleet Assignment for Order: {}", order.getOrderId());

        try {
            // 1. Fetch available drivers from Fleet Service
            List<DriverDto> availableDrivers = fleetClient.getAvailableDrivers();

            if (availableDrivers != null && !availableDrivers.isEmpty()) {
                // Simple strategy: Assign the first available driver
                DriverDto assignedDriver = availableDrivers.get(0);

                log.info("Driver found: {} (ID: {})", assignedDriver.getName(), assignedDriver.getId());

                job.setMatchedDriverId(String.valueOf(assignedDriver.getId()));
                job.setStatus(com.logistics.dispatch.model.DispatchStatus.ASSIGNED);
                job.setLastErrorMessage(null); // Clear errors

                return true;
            } else {
                log.warn("No available drivers found for B2B order.");
                job.setLastErrorMessage("No available dedicated drivers found.");
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to call Fleet Service", e);
            job.setLastErrorMessage("Fleet Service Unavailable: " + e.getMessage());
            return false;
        }
    }
}
