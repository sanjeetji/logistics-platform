package com.logistics.platform.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all platform events to ensure consistent metadata and schema
 * versioning.
 * Supports polymorphic deserialization based on schemaVersion and eventType.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType", visible = true, defaultImpl = Void.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreatedEvent.class, name = "ORDER_CREATED"),
        @JsonSubTypes.Type(value = OrderStatusChangedEvent.class, name = "ORDER_STATUS_CHANGED"),
        @JsonSubTypes.Type(value = DriverLocationUpdatedEvent.class, name = "DRIVER_LOCATION_UPDATED"),
        @JsonSubTypes.Type(value = RouteOptimizedEvent.class, name = "ROUTE_OPTIMIZED")
})
public abstract class BaseEvent {

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    @Builder.Default
    private String eventVersion = "1.0";

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Builder.Default
    private String schemaVersion = "1.0";

    private String eventType;
}
