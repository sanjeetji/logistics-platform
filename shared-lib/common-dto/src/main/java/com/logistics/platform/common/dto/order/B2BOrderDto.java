package com.logistics.platform.common.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BOrderDto {
    private String sapOrderId;
    private String clientId;
    private String status;
    private BigDecimal totalAmount;
    private List<B2BOrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class B2BOrderItemDto {
        private String sku;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
