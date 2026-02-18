package com.logistics.bff.unified.client.mobile;

import com.logistics.platform.dto.tracking.TrackingEventDTO;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {
    
    @GetMapping("/api/v1/tracking/{trackingNumber}")
    TrackingInfoDTO getTrackingByNumber(@PathVariable("trackingNumber") String trackingNumber);
    
    @GetMapping("/api/v1/tracking/order/{orderId}")
    TrackingInfoDTO getTrackingByOrder(@PathVariable("orderId") String orderId);
    
    @GetMapping("/api/v1/tracking/events/{orderId}")
    List<TrackingEventDTO> getTrackingEvents(@PathVariable("orderId") String orderId);
    
    @PostMapping("/api/v1/tracking/location")
    Object updateLocation(@RequestBody Object locationData);
}
