package com.logistics.orchestration.outbox;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String type; // Event Type

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload; // JSON Payload

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime processedAt;
}
