package com.logistics.b2b.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStopDTO {
    
    private Integer stopSequence;
    private String stopType; // PICKUP, DELIVERY
    private String address;
    private Double latitude;
    private Double longitude;
    private String contactName;
    private String contactPhone;
    private LocalDateTime estimatedArrival;
    private String items;
    private String notes;
}
