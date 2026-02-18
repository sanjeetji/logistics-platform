package com.logistics.platform.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStopDto {
    private Integer stopSequence;
    private String stopType; // PICKUP, DROP, RETURN
    private String address;
    private Double latitude;
    private Double longitude;
    private String contactName;
    private String contactPhone;
    private String instructions;
}
