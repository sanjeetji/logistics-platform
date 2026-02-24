package com.logistics.platform.common.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemFulfillmentDto {
    private String sku;
    private Integer fulfilledQuantity;
    private String status; // FULFILLED, UNAVAILABLE
}
