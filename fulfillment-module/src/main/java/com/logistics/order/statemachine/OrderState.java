package com.logistics.order.statemachine;

/**
 * Order state machine states
 * Represents all possible states an order can be in
 */
public enum OrderState {
    CREATED,
    VALIDATED,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    FAILED
}
