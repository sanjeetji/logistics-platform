package com.logistics.platform.common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {
    private String recipient;
    private String subject;
    private String content;
    private String type; // EMAIL, SMS, PUSH
    private String templateId; // For SendGrid/Twilio templates
    private Map<String, String> metaData; // Dynamic values
    private LocalDateTime timestamp;
}
