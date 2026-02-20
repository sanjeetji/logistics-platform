package com.logistics.fleet.statemachine;

public enum DriverState {
    OFFLINE,
    AVAILABLE,
    ASSIGNED,
    EN_ROUTE_PICKUP,
    AT_PICKUP,
    EN_ROUTE_DELIVERY,
    AT_DELIVERY,
    ON_BREAK
}
