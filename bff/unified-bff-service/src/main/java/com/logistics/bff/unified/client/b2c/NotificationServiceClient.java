package com.logistics.bff.unified.client.b2c;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    @GetMapping("/api/v1/notifications/user/{userId}")
    List<Object> getNotifications(@PathVariable("userId") String userId);

    @PutMapping("/api/v1/notifications/{id}/read")
    void markAsRead(@PathVariable("id") String id);

    @PostMapping("/api/v1/notifications/send")
    void sendNotification(@RequestBody Map<String, Object> notification);
}
