package com.logistics.order.statemachine;

/**
 * Order state machine events
 * Represents all possible events that can trigger state transitions
 */
public enum OrderEvent {
    VALIDATE,
    ASSIGN,
    PICKUP,
    START_TRANSIT,
    DELIVER,
    CANCEL,
    FAIL
}
