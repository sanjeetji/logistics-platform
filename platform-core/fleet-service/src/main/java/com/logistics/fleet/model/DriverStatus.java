package com.logistics.fleet.model;

public enum DriverStatus {
    OFFLINE,
    AVAILABLE,
    ASSIGNED,
    EN_ROUTE_PICKUP,
    AT_PICKUP,
    EN_ROUTE_DELIVERY,
    AT_DELIVERY,
    ON_BREAK,
    ONLINE, // Legacy support if needed
    ON_TRIP // Legacy support if needed
}
