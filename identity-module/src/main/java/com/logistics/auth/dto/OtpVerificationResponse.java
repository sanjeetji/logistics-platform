package com.logistics.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationResponse {
    private boolean success;
    private String message;
    private String phone;
    private String verificationStatus; // PENDING, APPROVED, FAILED
}
