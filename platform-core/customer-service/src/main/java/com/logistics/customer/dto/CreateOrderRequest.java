package com.logistics.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Pickup address ID is required")
    private Long pickupAddressId;
    
    @NotNull(message = "Drop address ID is required")
    private Long dropAddressId;
    
    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;
    
    private String notes;
    
    private Double weightKg;
    
    // Payment
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
