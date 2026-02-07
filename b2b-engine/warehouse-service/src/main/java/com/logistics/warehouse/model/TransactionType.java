package com.logistics.warehouse.model;

public enum TransactionType {
    IN, // Stock received
    OUT, // Stock dispatched
    ADJUSTMENT, // Manual adjustment
    RESERVE, // Reserved for order
    RELEASE // Reservation released
}
