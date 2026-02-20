package com.logistics.dispatch.engine;

import com.logistics.platform.common.dto.fleet.DriverLocationDto;
import com.logistics.platform.common.dto.order.TransportOrderDto;

public interface DispatchConstraint {
    boolean matches(TransportOrderDto order, DriverLocationDto driver);

    String reason();
}
