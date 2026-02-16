package com.logistics.routing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDTO {
    private String id;
    private LocationDTO pickupLocation;
    private LocationDTO deliveryLocation;
    private int weight;
    private int volume;
    private Integer serviceTimeMinutes;
    private Double pickupWindowStart;
    private Double pickupWindowEnd;
    private Double deliveryWindowStart;
    private Double deliveryWindowEnd;
    private Integer priority;
}
