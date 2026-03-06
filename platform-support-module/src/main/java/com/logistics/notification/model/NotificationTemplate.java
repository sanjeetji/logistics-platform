package com.logistics.notification.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationTemplate extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String templateId;

    @Column(nullable = false)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    private String subject; // For email

    @Column(columnDefinition = "text", nullable = false)
    private String body; // With placeholders like {{orderid}}, {{customername}}

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(columnDefinition = "text")
    private String description;
}
