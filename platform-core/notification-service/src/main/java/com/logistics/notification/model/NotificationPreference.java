package com.logistics.notification.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationPreference extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType userType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean smsEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean emailEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean pushEnabled = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean whatsappEnabled = false;
}
