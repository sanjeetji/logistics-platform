package com.logistics.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardExternalOrderRequest {

    private String externalOrderId;
    private String platform; // "SHOPIFY", "WOOCOMMERCE", "CUSTOM"

    // Customer Details
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Shipping Details (Drop Location)
    private String shippingAddress;
    private String city;
    private String zipCode;
    private String country;

    // Optional: Pre-calculated coordinates if available
    private Double latitude;
    private Double longitude;

    // Order Details
    private List<ExternalOrderItem> items;
    private BigDecimal totalPrice;
    private String currency; // "USD", "EUR"
    private Double totalWeightKg;

    // Scheduling and Preferences
    private String scheduledTime; // ISO-8601 string
    private String timeSlot;
    private String deliveryInstructions;
    private Boolean contactlessDelivery;
    private String safeDropLocation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalOrderItem {
        private String sku;
        private String name;
        private Integer quantity;
        private BigDecimal price;
    }
}
