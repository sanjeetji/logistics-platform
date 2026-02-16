package com.logistics.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEstimateRequest {

    @NotNull(message = "Pickup latitude is required")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    private Double pickupLongitude;

    @NotNull(message = "Drop latitude is required")
    private Double dropLatitude;

    @NotNull(message = "Drop longitude is required")
    private Double dropLongitude;

    @NotNull(message = "Vehicle type is required")
    private String vehicleType;

    private com.logistics.pricing.model.ServiceLevel serviceLevel; // Optional filter

    private String targetCurrency; // Optional, defaults to "INR" if null

    private LocalDateTime scheduledTime; // Optional, for future bookings

    private String orderId; // Optional, if estimate is for existing order

    // Dimensions in cm
    private Double length;
    private Double width;
    private Double height;

    // Weight in kg
    private Double weight;
    private Double volume;
    private DeliveryType deliveryType;

    public enum DeliveryType {
        STANDARD,
        EXPRESS,
        ECONOMY
    }
}
