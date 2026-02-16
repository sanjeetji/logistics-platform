package com.logistics.shipment.statemachine.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentStatusChangedEvent {
    private String shipmentId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp;
    private String reason;
}
