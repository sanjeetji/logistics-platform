package com.logistics.parcel.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parcel extends BaseEntity {

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
}
