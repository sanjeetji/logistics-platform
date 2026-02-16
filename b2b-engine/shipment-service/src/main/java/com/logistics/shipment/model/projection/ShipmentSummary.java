package com.logistics.shipment.model.projection;

import com.logistics.shipment.model.ShipmentStatus;
import java.time.LocalDateTime;

public interface ShipmentSummary {
    String getShipmentId();

    String getTenantId();

    ShipmentStatus getStatus();

    String getDriverId();

    String getVehicleId();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();
}
