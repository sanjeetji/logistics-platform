package com.logistics.fleet.model.projection;

import com.logistics.fleet.model.DriverStatus;

public interface DriverSummary {
    Long getId();

    String getName();

    String getPhoneNumber();

    DriverStatus getStatus();

    String getCurrentOrderId();
}
