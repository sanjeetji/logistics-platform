package com.logistics.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkDeliveredRequest {

    private String photoUrl; // Optional photo proof for safe drop deliveries
}
