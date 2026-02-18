package com.logistics.bff.unified.dto.b2c;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicTrackingResponse {
    private BrandInfo brandInfo;
    private String trackingNumber;
    private String orderId;
    private String currentStatus;
    private String estimatedDelivery;
    private ParcelDetails parcelDetails;
    private List<TrackingEvent> timeline;
    private LocationInfo currentLocation;
    private DriverInfo driverInfo;
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
        private String packageType;
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
        private String lastUpdated;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverInfo {
        private String firstName;
        private String phoneNumber;
        private Double rating;
        private String vehicleNumber;
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
