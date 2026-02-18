package com.logistics.platform.common.dto.parcel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParcelDTO {
    private Long id;
    private String trackingNumber;
    private String senderName;
    private String senderAddress;
    private String receiverName;
    private String receiverAddress;
    private BigDecimal weight;
    private Double length;
    private Double width;
    private Double height;
    private BigDecimal volumetricWeight;
    private BigDecimal chargeableWeight;
    private String status; // CREATED, PICKED_UP, IN_TRANSIT, DELIVERED
    private Long partnerId;
    private String partnerTrackingNumber;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
