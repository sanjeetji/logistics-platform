package com.logistics.dispatch.engine.impl;

import com.logistics.dispatch.engine.DispatchConstraint;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.springframework.stereotype.Component;

@Component
public class VehicleCapacityConstraint implements DispatchConstraint {

    @Override
    public boolean matches(TransportOrderDto order, DriverLocationDto driver) {
        if (order == null || driver == null) {
            return false;
        }

        double orderWeight = order.getWeightKg() != null ? order.getWeightKg() : 0.0;
        double vehicleCapacity = driver.getMaxCapacityKg() != null ? driver.getMaxCapacityKg() : 0.0;

        // If capacity is not set, we assume it's infinite or not yet synchronized
        // For strict matching, we might want to return false if capacity is unknown
        // But for now, if it's set, we enforce it.
        if (vehicleCapacity > 0) {
            return vehicleCapacity >= orderWeight;
        }

        return true;
    }

    @Override
    public String reason() {
        return "Vehicle capacity insufficient";
    }
}
