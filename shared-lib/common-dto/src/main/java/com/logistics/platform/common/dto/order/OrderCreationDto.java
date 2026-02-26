package com.logistics.platform.common.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreationDto {
    private String externalOrderId;
    private String orderType;
    private Double weightKg;
    private Double volumeM3;
}
