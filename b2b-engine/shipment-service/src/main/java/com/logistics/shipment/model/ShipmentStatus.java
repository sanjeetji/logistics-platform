package com.logistics.shipment.model;

public enum ShipmentStatus {
    CREATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    AT_HUB,
    OUT_FOR_DELIVERY,
    DELIVERED,
    COMPLETED, // Alias for DELIVERED or for compatibility
    RETURNED,
    CANCELLED
}
