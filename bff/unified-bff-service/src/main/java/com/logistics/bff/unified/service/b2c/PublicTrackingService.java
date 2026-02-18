package com.logistics.bff.unified.service.b2c;

import com.logistics.bff.unified.client.b2c.TrackingServiceClient;
import com.logistics.bff.unified.client.order.OrderServiceClient;
import com.logistics.bff.unified.client.b2c.TenantServiceClient;
import com.logistics.bff.unified.client.b2c.CustomerServiceClient;
import com.logistics.bff.unified.dto.b2c.PublicTrackingResponse;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.tracking.TrackingEventDTO;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for public tracking functionality
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PublicTrackingService {

    private final TrackingServiceClient trackingClient;
    private final OrderServiceClient orderClient;
    private final TenantServiceClient tenantClient;

    /**
     * Get public tracking information by tracking number
     */
    @Cacheable(value = "public-tracking", key = "#trackingNumber", unless = "#result == null")
    public PublicTrackingResponse getPublicTracking(String trackingNumber, String brandId) {
        log.info("Fetching public tracking for: {}, brand: {}", trackingNumber, brandId);
        try {
            // 1. Fetch Order by Tracking Number
            OrderDTO order = orderClient.getOrderByTrackingNumber(trackingNumber);
            if (order == null)
                return buildErrorResponse(trackingNumber, brandId);

            String orderId = order.getOrderId();

            // 2. Fetch Tracking Events
            List<TrackingEventDTO> events = trackingClient.getTrackingEvents(orderId);

            // 3. Fetch current tracking info (location, status)
            TrackingInfoDTO trackingInfo = trackingClient.getTrackingInfo(orderId);

            // 4. Build response
            return PublicTrackingResponse.builder()
                    .brandInfo(buildBrandInfo(brandId))
                    .trackingNumber(trackingNumber)
                    .orderId(orderId)
                    .currentStatus(trackingInfo != null ? trackingInfo.getCurrentStatus() : order.getStatus())
                    .estimatedDelivery(
                            order.getEstimatedDelivery() != null ? order.getEstimatedDelivery().toString() : "TBD")
                    .parcelDetails(buildParcelDetails(order))
                    .timeline(buildTimeline(events))
                    .currentLocation(buildLocationInfo(trackingInfo))
                    .supportContact(buildSupportContact(brandId))
                    .build();
        } catch (Exception e) {
            log.error("Error fetching public tracking", e);
            return buildErrorResponse(trackingNumber, brandId);
        }
    }

    private PublicTrackingResponse.BrandInfo buildBrandInfo(String brandId) {
        return PublicTrackingResponse.BrandInfo.builder()
                .brandName("Logistics Platform")
                .logoUrl("https://cdn.example.com/logo.png")
                .primaryColor("#2563eb")
                .secondaryColor("#1e40af")
                .websiteUrl("https://example.com")
                .build();
    }

    private PublicTrackingResponse.ParcelDetails buildParcelDetails(OrderDTO order) {
        return PublicTrackingResponse.ParcelDetails.builder()
                .description("Order #" + order.getOrderId())
                .weight(order.getWeight() != null ? order.getWeight() + " kg" : "N/A")
                .packageType(order.getPackageType() != null ? order.getPackageType() : "Parcel")
                .specialInstructions(order.getSpecialInstructions())
                .build();
    }

    private List<PublicTrackingResponse.TrackingEvent> buildTimeline(List<TrackingEventDTO> events) {
        if (events == null)
            return new ArrayList<>();
        return events.stream()
                .map(event -> PublicTrackingResponse.TrackingEvent.builder()
                        .status(event.getStatus())
                        .description(event.getDescription())
                        .timestamp(event.getTimestamp())
                        .location(event.getLocation())
                        .icon("package")
                        .build())
                .collect(Collectors.toList());
    }

    private PublicTrackingResponse.LocationInfo buildLocationInfo(TrackingInfoDTO tracking) {
        if (tracking == null || tracking.getCurrentLatitude() == null)
            return null;
        return PublicTrackingResponse.LocationInfo.builder()
                .latitude(tracking.getCurrentLatitude())
                .longitude(tracking.getCurrentLongitude())
                .address(tracking.getCurrentLocation())
                .lastUpdated("Just now")
                .build();
    }

    private PublicTrackingResponse.ContactInfo buildSupportContact(String brandId) {
        return PublicTrackingResponse.ContactInfo.builder()
                .phone("+91-1800-LOGISTICS")
                .email("support@logistics.example.com")
                .helplineHours("24/7")
                .build();
    }

    private PublicTrackingResponse buildErrorResponse(String trackingNumber, String brandId) {
        return PublicTrackingResponse.builder()
                .trackingNumber(trackingNumber)
                .currentStatus("NOT_FOUND")
                .brandInfo(buildBrandInfo(brandId))
                .build();
    }
}
