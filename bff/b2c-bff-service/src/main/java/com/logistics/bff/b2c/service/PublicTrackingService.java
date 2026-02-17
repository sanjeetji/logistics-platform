package com.logistics.bff.b2c.service;

import com.logistics.bff.b2c.client.FleetServiceClient;
import com.logistics.bff.b2c.client.OrderServiceClient;
import com.logistics.bff.b2c.client.TenantServiceClient;
import com.logistics.bff.b2c.client.TrackingServiceClient;
import com.logistics.bff.b2c.dto.PublicTrackingResponse;
import com.logistics.platform.dto.fleet.DriverDTO;
import com.logistics.platform.dto.order.OrderDTO;
import com.logistics.platform.dto.tracking.TrackingEventDTO;
import com.logistics.platform.dto.tracking.TrackingInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private final FleetServiceClient fleetClient;
    private final TenantServiceClient tenantClient;

    /**
     * Get public tracking information by tracking number
     * Cached for 1 minute to reduce load
     * 
     * @param trackingNumber Unique tracking number
     * @param brandId        Optional brand ID for white-labeling
     * @return Public tracking page data
     */
    @Cacheable(value = "publicTracking", key = "#trackingNumber", unless = "#result == null")
    public PublicTrackingResponse getPublicTracking(String trackingNumber, String brandId) {
        log.info("Fetching public tracking for: {}, brandId: {}", trackingNumber, brandId);

        try {
            // Fetch tracking data
            TrackingInfoDTO trackingInfo = trackingClient.getTrackingByNumber(trackingNumber);
            String orderId = trackingInfo != null ? trackingInfo.getOrderId() : null;

            List<TrackingEventDTO> trackingEvents = orderId != null ? trackingClient.getTrackingEvents(orderId)
                    : Collections.emptyList();

            // Fetch order data (for parcel details)
            OrderDTO order = orderClient.getOrderByTrackingNumber(trackingNumber);

            // Fetch driver data if available
            DriverDTO driver = null;
            if (order != null && order.getDriverId() != null) {
                try {
                    driver = fleetClient.getDriverById(Long.parseLong(order.getDriverId()));
                } catch (Exception e) {
                    log.warn("Failed to fetch driver info for ID: {}", order.getDriverId());
                }
            }

            // Build brand info (fetch from brand service or use defaults)
            var brandInfo = buildBrandInfo(brandId);

            // Build response
            return PublicTrackingResponse.builder()
                    .brandInfo(brandInfo)
                    .trackingNumber(trackingNumber)
                    .orderId(order != null ? order.getId() : orderId)
                    .currentStatus(trackingInfo != null ? trackingInfo.getCurrentStatus()
                            : (order != null ? order.getStatus() : "UNKNOWN"))
                    .estimatedDelivery(extractETA(trackingInfo, order))
                    .parcelDetails(buildParcelDetails(order))
                    .timeline(buildTimeline(trackingEvents))
                    .currentLocation(buildLocationInfo(trackingInfo))
                    .driverInfo(buildDriverInfo(driver))
                    .supportContact(buildSupportContact(brandId))
                    .build();

        } catch (Exception e) {
            log.error("Failed to fetch public tracking for: {}", trackingNumber, e);
            return buildErrorResponse(trackingNumber, brandId);
        }
    }

    private PublicTrackingResponse.BrandInfo buildBrandInfo(String brandId) {
        try {
            if (brandId != null) {
                // Assuming brandId is the tenantId or mapped to it
                // For now, treating brandId as tenantId
                Long tenantId = Long.parseLong(brandId);
                var tenant = tenantClient.getTenantById(tenantId);

                if (tenant != null && tenant.getConfig() != null) {
                    var config = tenant.getConfig();
                    return PublicTrackingResponse.BrandInfo.builder()
                            .brandName(config.getBrandName() != null ? config.getBrandName() : tenant.getName())
                            .logoUrl(config.getLogoUrl() != null ? config.getLogoUrl()
                                    : "https://cdn.example.com/logo.png")
                            .primaryColor(config.getPrimaryColor() != null ? config.getPrimaryColor() : "#2563eb")
                            .secondaryColor(config.getSecondaryColor() != null ? config.getSecondaryColor() : "#1e40af")
                            .websiteUrl(config.getWebsiteUrl() != null ? config.getWebsiteUrl() : "https://example.com")
                            .build();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch brand info for id: {}", brandId, e);
        }

        // Default branding
        return PublicTrackingResponse.BrandInfo.builder()
                .brandName("Logistics Platform")
                .logoUrl("https://cdn.example.com/logo.png")
                .primaryColor("#2563eb")
                .secondaryColor("#1e40af")
                .websiteUrl("https://example.com")
                .build();
    }

    private PublicTrackingResponse.ParcelDetails buildParcelDetails(OrderDTO order) {
        if (order == null)
            return null;
        return PublicTrackingResponse.ParcelDetails.builder()
                .description("Order #" + order.getId())
                .weight(order.getWeight() != null ? order.getWeight() + " kg" : "N/A")
                .packageType(order.getPackageType() != null ? order.getPackageType() : "Parcel")
                .specialInstructions(order.getSpecialInstructions())
                .build();
    }

    private List<PublicTrackingResponse.TrackingEvent> buildTimeline(List<TrackingEventDTO> events) {
        if (events == null)
            return Collections.emptyList();
        return events.stream()
                .map(event -> PublicTrackingResponse.TrackingEvent.builder()
                        .status(event.getStatus())
                        .description(event.getDescription())
                        .timestamp(event.getTimestamp())
                        .location(event.getLocation())
                        .icon(getIconForStatus(event.getStatus()))
                        .build())
                .collect(Collectors.toList());
    }

    private String getIconForStatus(String status) {
        if (status == null)
            return "info-circle";
        return switch (status) {
            case "ORDER_PLACED", "CREATED" -> "plus-circle";
            case "CONFIRMED" -> "check-circle";
            case "PICKED_UP" -> "box";
            case "IN_TRANSIT" -> "truck";
            case "DELIVERED" -> "home";
            case "CANCELLED" -> "times-circle";
            default -> "info-circle";
        };
    }

    private PublicTrackingResponse.LocationInfo buildLocationInfo(TrackingInfoDTO tracking) {
        if (tracking == null || tracking.getCurrentLatitude() == null)
            return null;
        return PublicTrackingResponse.LocationInfo.builder()
                .latitude(tracking.getCurrentLatitude())
                .longitude(tracking.getCurrentLongitude())
                .address(tracking.getCurrentLocation())
                .lastUpdated(tracking.getLastUpdated() != null ? tracking.getLastUpdated().toString() : "Just now")
                .build();
    }

    private PublicTrackingResponse.DriverInfo buildDriverInfo(DriverDTO driver) {
        if (driver == null)
            return null;
        return PublicTrackingResponse.DriverInfo.builder()
                .firstName(driver.getFirstName() != null ? driver.getFirstName() : "Driver")
                .phoneNumber(maskPhoneNumber(driver.getPhoneNumber()))
                .rating(driver.getRating())
                .vehicleNumber("N/A") // Extract if available in DriverDTO
                .build();
    }

    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 10)
            return phone;
        // Format: +91-XXXX-XX-5678 or similar
        return phone.substring(0, phone.length() - 8) + "XXXX-XX-" + phone.substring(phone.length() - 4);
    }

    private PublicTrackingResponse.ContactInfo buildSupportContact(String brandId) {
        try {
            if (brandId != null) {
                Long tenantId = Long.parseLong(brandId);
                var tenant = tenantClient.getTenantById(tenantId);

                if (tenant != null && tenant.getConfig() != null) {
                    var config = tenant.getConfig();
                    return PublicTrackingResponse.ContactInfo.builder()
                            .phone(config.getSupportPhone() != null ? config.getSupportPhone() : "+91-1800-XXX-XXXX")
                            .email(config.getSupportEmail() != null ? config.getSupportEmail() : "support@example.com")
                            .helplineHours("24/7")
                            .build();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch support contact for id: {}", brandId);
        }

        return PublicTrackingResponse.ContactInfo.builder()
                .phone("+91-1800-XXX-XXXX")
                .email("support@example.com")
                .helplineHours("24/7")
                .build();
    }

    private String extractETA(TrackingInfoDTO tracking, OrderDTO order) {
        if (tracking != null && tracking.getEstimatedDelivery() != null) {
            return tracking.getEstimatedDelivery().toString();
        }
        if (order != null && order.getEstimatedDelivery() != null) {
            return order.getEstimatedDelivery().toString();
        }
        return "TBD";
    }

    private PublicTrackingResponse buildErrorResponse(String trackingNumber, String brandId) {
        return PublicTrackingResponse.builder()
                .brandInfo(buildBrandInfo(brandId))
                .trackingNumber(trackingNumber)
                .currentStatus("NOT_FOUND")
                .build();
    }
}
