package com.logistics.sla.client;

import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {

    @PostMapping("/api/v1/notifications/send")
    ApiResponse<String> sendNotification(@RequestBody SendNotificationRequest request);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SendNotificationRequest {
        private String recipientId;
        private String recipientType; // USER, DRIVER, CLIENT
        private String channel; // SMS, EMAIL, PUSH, WHATSAPP
        private String subject;
        private String body;
        private Map<String, Object> metadata;
    }
}
