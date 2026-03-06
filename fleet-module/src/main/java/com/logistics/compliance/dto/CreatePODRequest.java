package com.logistics.compliance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePODRequest {

    @NotNull(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Recipient name is required")
    private String recipientName;

    @NotNull(message = "Signature is required")
    private String signature; // Base64 encoded signature

    private Double latitude;

    private Double longitude;

    private String photoUrl;

    private String notes;
}
