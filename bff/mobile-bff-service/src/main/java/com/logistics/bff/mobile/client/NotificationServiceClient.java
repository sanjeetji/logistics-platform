package com.logistics.bff.mobile.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
    
    @GetMapping("/api/v1/notifications/user/{userId}")
    List<Map<String, Object>> getUserNotifications(@PathVariable("userId") String userId,
                                                    @RequestParam(required = false) Boolean unreadOnly);
}
