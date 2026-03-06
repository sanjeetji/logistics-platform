package com.logistics.platform.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseOrderPackedEvent {
    private String orderId;
    private Long warehouseId;
    private LocalDateTime packedAt;
    private String destinationRegion;
}
