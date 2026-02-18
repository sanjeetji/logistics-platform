package com.logistics.bff.unified.client.b2c;

import com.logistics.platform.dto.tracking.TrackingEventDTO;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {
    @GetMapping("/api/v1/tracking/{trackingNumber}")
    TrackingInfoDTO getTrackingByNumber(@PathVariable("trackingNumber") String trackingNumber);

    @GetMapping("/api/v1/tracking/order/{orderId}")
    TrackingInfoDTO getTrackingInfo(@PathVariable("orderId") String orderId);

    @GetMapping("/api/v1/tracking/events/{orderId}")
    List<TrackingEventDTO> getTrackingEvents(@PathVariable("orderId") String orderId);

    @GetMapping("/api/v1/tracking/live/{orderId}")
    TrackingInfoDTO getLiveTracking(@PathVariable("orderId") String orderId);
}
