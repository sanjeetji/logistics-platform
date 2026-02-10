package com.logistics.platform.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    private String customerType; // B2B, B2C
    private String companyName; // For B2B customers
    private String tenantId;
}
