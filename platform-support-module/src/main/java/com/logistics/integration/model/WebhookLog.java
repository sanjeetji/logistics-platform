package com.logistics.integration.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity(name = "IntegrationWebhookLog")
@Table(name = "webhook_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;

    @Enumerated(EnumType.STRING)
    private EcommercePlatform platform;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String eventType; // e.g., orders/create

    private String status; // RECEIVED, PROCESSED, FAILED

    private String errorMessage;

    private LocalDateTime receivedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
    }
}
