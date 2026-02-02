package com.logistics.dispatch.strategy;

import com.logistics.platform.api.fleet.FleetClient;
import com.logistics.platform.api.order.OrderClient;
import com.logistics.platform.common.dto.fleet.DriverDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("b2bAssignmentStrategy")
@RequiredArgsConstructor
public class B2BAssignmentStrategy implements DispatchStrategy {

    private final FleetClient fleetClient;
    private final OrderClient orderClient;

    @Override
    public void dispatchOrder(Long orderId) {
        log.info("Executing B2B Assignment Strategy for Order ID: {}", orderId);
        
        TransportOrderDto order = orderClient.getOrderById(orderId);
        log.info("Fetched Order Details for Tenant: {}", order.getTenantId());

        // Simple logic: Find first ONLINE driver
        DriverDto candidate = fleetClient.getAllDrivers().stream()
                .filter(d -> "ONLINE".equals(d.getStatus()))
                .findFirst()
                .orElse(null);

        if (candidate != null) {
            log.info("Assigning Driver {} to Order {}", candidate.getName(), orderId);
        } else {
            log.warn("No available drivers found for B2B order assignment.");
        }
    }
}
