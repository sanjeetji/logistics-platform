package com.logistics.platform.common.dto.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private Long id;
    private String licensePlate;
    private String conditions;
    private String type;
    private Double capacityKg;
    private Double volumeCubicMeter;
    private boolean active;
    private Long currentDriverId;
}
