package com.logistics.platform.common.dto.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTimePredictionRequest {
    private double pickupLat;
    private double pickupLng;
    private double deliveryLat;
    private double deliveryLng;
    private String vehicleType;
    private String timeOfDay;
    private String weatherCondition;
}
