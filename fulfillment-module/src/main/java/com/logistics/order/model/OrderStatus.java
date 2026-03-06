package com.logistics.order.model;

public enum OrderStatus {
    CREATED,
    ASSIGNED,
    PICKED_UP,
    PARTIALLY_PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    PARTIALLY_DELIVERED,
    CANCELLED,
    BACKORDERED
}
