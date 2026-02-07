package com.logistics.fleet.model;

public enum DriverStatus {
    OFFLINE, // Driver is offline
    ONLINE, // Driver is online and available
    ON_TRIP, // Driver is currently on a trip
    AVAILABLE // Driver is available for assignment (alias for ONLINE)
}
