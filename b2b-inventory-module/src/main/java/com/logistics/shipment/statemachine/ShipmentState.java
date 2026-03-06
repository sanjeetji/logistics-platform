package com.logistics.shipment.statemachine;

public enum ShipmentState {
    CREATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    AT_HUB,
    OUT_FOR_DELIVERY,
    DELIVERED,
    RETURNED,
    CANCELLED
}
