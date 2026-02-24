package com.logistics.routing.rerouting;

/**
 * Re-routing Trigger Types
 */
public enum ReRoutingTrigger {
    TRAFFIC_INCIDENT, // Major traffic incident detected
    DRIVER_DELAY, // Driver running significantly behind schedule
    NEW_URGENT_ORDER, // New high-priority order needs insertion
    DELIVERY_FAILURE, // Delivery attempt failed, need to reschedule
    DRIVER_BREAK, // Driver needs mandatory break
    WEATHER_ALERT, // Severe weather alert affecting route
    INVENTORY_REPLENISHMENT // Recovered orders from backorder
}
