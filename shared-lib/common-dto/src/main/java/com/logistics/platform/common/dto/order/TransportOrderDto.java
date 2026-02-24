package com.logistics.platform.common.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportOrderDto {
    private Long id;
    private String orderId;
    private String customerId;
    private String tenantId;
    private String orderType;
    private String status;
    private String pickupAddress;
    private Double pickupLat;
    private Double pickupLng;
    private String dropAddress;
    private Double dropLat;
    private Double dropLng;
    private Double weightKg;
    private Double price;
    private String requiredVehicleType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
