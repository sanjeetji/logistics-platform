package com.logistics.analytics.dto;

import com.logistics.analytics.model.EntityType;
import com.logistics.analytics.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage {
    private EventType eventType;
    private String entityId;
    private EntityType entityType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
