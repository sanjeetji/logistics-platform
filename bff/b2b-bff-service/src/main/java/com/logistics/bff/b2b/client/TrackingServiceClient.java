package com.logistics.bff.b2b.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.logistics.platform.dto.tracking.*;

import java.util.List;

@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {
    
    @GetMapping("/api/v1/tracking/order/{orderId}")
    List<TrackingEventDTO> getTrackingByOrder(@PathVariable("orderId") Long orderId);
    
    @GetMapping("/api/v1/tracking/{trackingNumber}")
    TrackingInfoDTO getTrackingByNumber(@PathVariable("trackingNumber") String trackingNumber);
    
    @GetMapping("/api/v1/tracking/order/{orderId}")
    TrackingInfoDTO getTrackingInfo(@PathVariable("orderId") String orderId);
}
