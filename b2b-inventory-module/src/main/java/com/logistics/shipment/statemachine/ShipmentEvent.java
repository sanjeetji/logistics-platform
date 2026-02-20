package com.logistics.shipment.statemachine;

public enum ShipmentEvent {
    ASSIGN,
    PICKUP,
    START_TRANSIT,
    ARRIVE_HUB,
    DEPART_HUB,
    OUT_FOR_DELIVERY,
    DELIVER,
    RETURN,
    CANCEL
}
