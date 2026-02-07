package com.logistics.analytics.model;

public enum EventType {
    // Order events
    ORDER_CREATED,
    ORDER_ASSIGNED,
    ORDER_PICKED_UP,
    ORDER_IN_TRANSIT,
    ORDER_DELIVERED,
    ORDER_CANCELLED,
    
    // Payment events
    PAYMENT_INITIATED,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    
    // Driver events
    DRIVER_SHIFT_STARTED,
    DRIVER_SHIFT_ENDED,
    DRIVER_JOB_ACCEPTED,
    DRIVER_JOB_REJECTED,
    
    // SLA events
    SLA_BREACHED,
    SLA_AT_RISK,
    
    // Other
    CUSTOMER_REGISTERED,
    VEHICLE_ASSIGNED
}
