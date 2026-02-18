package com.logistics.bff.unified.dto.b2c;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Public tracking page response for B2B customers
 * Displayed on web-based tracking link with brand customization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicTrackingResponse {
    
    // Brand Information (for white-labeling)
    private BrandInfo brandInfo;
    
    // Order Information
    private String trackingNumber;
    private String orderId;
    private String currentStatus;
    private String estimatedDelivery;
    
    // Parcel Details
    private ParcelDetails parcelDetails;
    
    // Tracking Timeline
    private List<TrackingEvent> timeline;
    
    // Current Location
    private LocationInfo currentLocation;
    
    // Driver Information (optional, based on privacy settings)
    private DriverInfo driverInfo;
    
    // Contact Information
    private ContactInfo supportContact;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandInfo {
        private String brandName;
        private String logoUrl;
        private String primaryColor;
        private String secondaryColor;
        private String websiteUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParcelDetails {
        private String description;
        private String weight;
        private String dimensions;
        private String packageType;
        private Integer quantity;
        private String specialInstructions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEvent {
        private String status;
        private String description;
        private LocalDateTime timestamp;
        private String location;
        private String icon;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private Double latitude;
        private Double longitude;
        private String address;
        private String city;
        private String lastUpdated;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInfo {
        private String firstName;
        private String vehicleNumber;
        private String phoneNumber; // Masked: +91-XXXX-XX-1234
        private Double rating;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfo {
        private String phone;
        private String email;
        private String helplineHours;
    }
}
