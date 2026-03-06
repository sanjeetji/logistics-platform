package com.logistics.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPreferencesRequest {

    private String deliveryInstructions;

    private Boolean contactlessDelivery;

    private LocalDateTime preferredDeliveryTimeStart;

    private LocalDateTime preferredDeliveryTimeEnd;

    private String safeDropLocation;
}
