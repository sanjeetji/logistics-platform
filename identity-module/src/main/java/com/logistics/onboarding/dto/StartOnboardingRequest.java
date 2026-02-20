package com.logistics.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartOnboardingRequest {
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    private String companyEmail;
    
    private String companyPhone;
    
    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;
    
    @Email(message = "Invalid email format")
    private String contactPersonEmail;
    
    private String businessType; // B2B, B2C, BOTH
    private String industry;
    private Integer expectedMonthlyOrders;
    private String country;
    private String city;
}
