package com.logistics.platform.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofenceEvent {
    private String driverId;
    private Long geofenceId;
    private String geofenceName;
    private GeofenceEventType eventType; // ENTER, EXIT
    private String associatedEntityId;
    private String associatedEntityType;
    private GeofencePurpose purpose;
    private LocalDateTime timestamp;
}
