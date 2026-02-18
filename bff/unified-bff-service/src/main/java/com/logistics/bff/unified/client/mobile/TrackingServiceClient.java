package com.logistics.bff.unified.client.mobile;

import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {
    @GetMapping("/api/v1/tracking/{trackingNumber}")
    TrackingInfoDTO getTrackingByNumber(@PathVariable("trackingNumber") String trackingNumber);

    @GetMapping("/api/v1/tracking/order/{orderId}")
    TrackingInfoDTO getTrackingByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/api/v1/tracking/events/{orderId}")
    Object getTrackingEvents(@PathVariable("orderId") String orderId);

    @PostMapping("/api/v1/tracking/location")
    void updateLocation(@RequestBody Map<String, Object> locationData);
}
