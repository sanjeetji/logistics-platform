package com.logistics.bff.unified.client.b2b;

import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {
    @GetMapping("/api/v1/tracking/order/{orderId}")
    TrackingInfoDTO getTrackingByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/api/v1/tracking/{trackingNumber}")
    TrackingInfoDTO getTrackingByNumber(@PathVariable("trackingNumber") String trackingNumber);
}
