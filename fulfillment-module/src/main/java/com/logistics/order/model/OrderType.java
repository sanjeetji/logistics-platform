package com.logistics.order.model;

public enum OrderType {
    B2C_ON_DEMAND, // Porter style (Point A to B)
    B2B_SHIPMENT, // Scheduled / Contract / Multi-stop
    HUB_TRANSFER, // Blue Dart style (Warehouse to Warehouse)
    REVERSE_PICKUP // Customer to Warehouse logic
}
