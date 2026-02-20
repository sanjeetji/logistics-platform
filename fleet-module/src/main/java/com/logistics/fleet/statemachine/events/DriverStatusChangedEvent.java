package com.logistics.fleet.statemachine.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusChangedEvent {
    private String driverEmail;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime timestamp;
    private String reason;
}
