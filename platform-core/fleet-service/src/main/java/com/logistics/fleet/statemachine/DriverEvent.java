package com.logistics.fleet.statemachine;

public enum DriverEvent {
    GO_ONLINE,
    ASSIGN,
    START_PICKUP,
    ARRIVE_PICKUP,
    START_DELIVERY,
    ARRIVE_DELIVERY,
    TAKE_BREAK,
    END_BREAK,
    GO_OFFLINE,
    REJECT_ASSIGNMENT,
    COMPLETE_DELIVERY
}
