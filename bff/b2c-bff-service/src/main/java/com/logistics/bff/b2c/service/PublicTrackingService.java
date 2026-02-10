package com.logistics.bff.b2c.service;

import com.logistics.bff.b2c.client.OrderServiceClient;
import com.logistics.bff.b2c.client.TrackingServiceClient;
import com.logistics.bff.b2c.dto.PublicTrackingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for public tracking functionality
 * Used by B2B customers to track parcels via web link
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PublicTrackingService {

    private final TrackingServiceClient trackingClient;
    private final OrderServiceClient orderClient;

    /**
     * Get public tracking information by tracking number
     * Cached for 1 minute to reduce load
     * 
     * @param trackingNumber Unique tracking number
     * @param brandId Optional brand ID for white-labeling
     * @return Public tracking page data
     */
    @Cacheable(value = "publicTracking", key = "#trackingNumber", unless = "#result == null")
    public PublicTrackingResponse getPublicTracking(String trackingNumber, String brandId) {
        log.info("Fetching public tracking for: {}, brandId: {}", trackingNumber, brandId);

        try {
            // Fetch tracking data
            var trackingInfo = trackingClient.getTrackingByNumber(trackingNumber);
            var trackingEvents = trackingClient.getTrackingEvents(trackingNumber);
            var liveLocation = trackingClient.getLiveLocation(trackingNumber);
            
            // Fetch order data (for parcel details)
            var order = orderClient.getOrderByTrackingNumber(trackingNumber);

            // Build brand info (fetch from brand service or use defaults)
            var brandInfo = buildBrandInfo(brandId);

            // Build response
            return PublicTrackingResponse.builder()
                .brandInfo(brandInfo)
                .trackingNumber(trackingNumber)
                .orderId(extractOrderId(order))
                .currentStatus(extractStatus(trackingInfo))
                .estimatedDelivery(extractETA(trackingInfo))
                .parcelDetails(buildParcelDetails(order))
                .timeline(buildTimeline(trackingEvents))
                .currentLocation(buildLocationInfo(liveLocation))
                .driverInfo(buildDriverInfo(order))
                .supportContact(buildSupportContact(brandId))
                .build();

        } catch (Exception e) {
            log.error("Failed to fetch public tracking for: {}", trackingNumber, e);
            return buildErrorResponse(trackingNumber, brandId);
        }
    }

    private PublicTrackingResponse.BrandInfo buildBrandInfo(String brandId) {
        // TODO: Fetch from brand service
        return PublicTrackingResponse.BrandInfo.builder()
            .brandName("Logistics Platform")
            .logoUrl("https://cdn.example.com/logo.png")
            .primaryColor("#2563eb")
            .secondaryColor("#1e40af")
            .websiteUrl("https://example.com")
            .build();
    }

    private PublicTrackingResponse.ParcelDetails buildParcelDetails(Object order) {
        // TODO: Extract from order object
        return PublicTrackingResponse.ParcelDetails.builder()
            .description("Package")
            .weight("2.5 kg")
            .dimensions("30x20x15 cm")
            .packageType("Box")
            .quantity(1)
            .specialInstructions("Handle with care")
            .build();
    }

    private List<PublicTrackingResponse.TrackingEvent> buildTimeline(Object events) {
        // TODO: Transform tracking events
        return List.of(
            PublicTrackingResponse.TrackingEvent.builder()
                .status("ORDER_PLACED")
                .description("Order placed successfully")
                .timestamp(LocalDateTime.now().minusHours(2))
                .location("Mumbai")
                .icon("check-circle")
                .build(),
            PublicTrackingResponse.TrackingEvent.builder()
                .status("IN_TRANSIT")
                .description("Package is on the way")
                .timestamp(LocalDateTime.now().minusHours(1))
                .location("Thane")
                .icon("truck")
                .build()
        );
    }

    private PublicTrackingResponse.LocationInfo buildLocationInfo(Object location) {
        // TODO: Extract from live location
        return PublicTrackingResponse.LocationInfo.builder()
            .latitude(19.0760)
            .longitude(72.8777)
            .address("Andheri East")
            .city("Mumbai")
            .lastUpdated("2 minutes ago")
            .build();
    }

    private PublicTrackingResponse.DriverInfo buildDriverInfo(Object order) {
        // TODO: Extract driver info (with privacy masking)
        return PublicTrackingResponse.DriverInfo.builder()
            .firstName("Rajesh")
            .vehicleNumber("MH-02-XX-1234")
            .phoneNumber("+91-XXXX-XX-5678") // Masked
            .rating(4.8)
            .build();
    }

    private PublicTrackingResponse.ContactInfo buildSupportContact(String brandId) {
        // TODO: Fetch from brand service
        return PublicTrackingResponse.ContactInfo.builder()
            .phone("+91-1800-XXX-XXXX")
            .email("support@example.com")
            .helplineHours("24/7")
            .build();
    }

    private String extractOrderId(Object order) {
        // TODO: Extract from order
        return "ORD-12345";
    }

    private String extractStatus(Object tracking) {
        // TODO: Extract from tracking
        return "IN_TRANSIT";
    }

    private String extractETA(Object tracking) {
        // TODO: Extract from tracking
        return "Today, 6:00 PM";
    }

    private PublicTrackingResponse buildErrorResponse(String trackingNumber, String brandId) {
        return PublicTrackingResponse.builder()
            .brandInfo(buildBrandInfo(brandId))
            .trackingNumber(trackingNumber)
            .currentStatus("NOT_FOUND")
            .build();
    }
}
