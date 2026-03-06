package com.logistics.notification.dto;

import com.logistics.notification.model.NotificationChannel;
import com.logistics.notification.model.RecipientType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    
    @NotNull(message = "Recipient ID is required")
    private String recipientId;
    
    @NotNull(message = "Recipient type is required")
    private RecipientType recipientType;
    
    @NotNull(message = "Channel is required")
    private NotificationChannel channel;
    
    private String subject;
    
    @NotNull(message = "Body is required")
    private String body;
    
    private Map<String, Object> metadata;
}
