package com.logistics.integration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;

    private String tenantId;

    @Enumerated(EnumType.STRING)
    private EcommercePlatform platform;

    private String targetUrl;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String eventType; // e.g., order.created, shipment.updated

    @Enumerated(EnumType.STRING)
    private WebhookStatus status; // PENDING, DELIVERED, FAILED

    private Integer retryCount;

    private String lastError;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = WebhookStatus.PENDING;
        retryCount = 0;
    }

    public enum WebhookStatus {
        PENDING,
        DELIVERED,
        FAILED
    }
}
