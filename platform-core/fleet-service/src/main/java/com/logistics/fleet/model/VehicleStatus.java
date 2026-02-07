package com.logistics.fleet.model;

public enum VehicleStatus {
    AVAILABLE, // Vehicle is available for assignment
    ASSIGNED, // Vehicle is assigned to a driver
    IN_USE, // Vehicle is currently in use on a trip
    MAINTENANCE, // Vehicle is under maintenance
    OUT_OF_SERVICE // Vehicle is out of service
}
