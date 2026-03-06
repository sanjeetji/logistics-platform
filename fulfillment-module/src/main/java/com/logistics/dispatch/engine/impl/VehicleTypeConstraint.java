package com.logistics.dispatch.engine.impl;

import com.logistics.dispatch.engine.DispatchConstraint;
import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.springframework.stereotype.Component;

@Component
public class VehicleTypeConstraint implements DispatchConstraint {

    @Override
    public boolean matches(TransportOrderDto order, DriverLocationDto driver) {
        if (order == null || driver == null) {
            return false;
        }

        String requiredType = order.getRequiredVehicleType();
        String availableType = driver.getVehicleType();

        // If no specific vehicle type is required, any vehicle is eligible
        if (requiredType == null || requiredType.isEmpty()) {
            return true;
        }

        // Exact match for now. In a more complex system, we could have type hierarchies
        // (e.g., a TRUCK can fulfill an order requiring a VAN).
        return requiredType.equalsIgnoreCase(availableType);
    }

    @Override
    public String reason() {
        return "Required vehicle type mismatch";
    }
}
