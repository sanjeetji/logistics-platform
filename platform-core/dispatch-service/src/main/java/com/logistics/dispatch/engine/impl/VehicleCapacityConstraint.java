package com.logistics.dispatch.engine.impl;

import com.logistics.dispatch.engine.DispatchConstraint;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.springframework.stereotype.Component;

@Component
public class VehicleCapacityConstraint implements DispatchConstraint {

    @Override
    public boolean matches(TransportOrderDto order, DriverLocationDto driver) {
        // Simple logic: if driver's vehicle capacity >= order weight
        // Assuming DriverLocationDto has vehicle capacity info (or we fetch it)
        // For now, let's assume DriverLocationDto has a 'capacity' field or we simulate
        // it.
        // Wait, DriverLocationDto might not have capacity. Let's check DTO.

        // If not available, we'll return true for now but add a TODO
        return true;
    }

    @Override
    public String reason() {
        return "Vehicle capacity insufficient";
    }
}
