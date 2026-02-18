package com.logistics.bff.unified.client.mobile;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    @GetMapping("/api/v1/notifications/user/{userId}")
    List<Object> getNotifications(@PathVariable("userId") String userId);

    @PutMapping("/api/v1/notifications/{id}/read")
    void markAsRead(@PathVariable("id") String id);
}
