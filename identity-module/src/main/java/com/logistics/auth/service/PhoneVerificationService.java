package com.logistics.auth.service;

import com.logistics.auth.dto.OtpVerificationResponse;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PhoneVerificationService {

    @Value("${twilio.verify-service-sid:mock-sid}")
    private String verifyServiceSid;

    @Value("${application.testing.static-otp:}")
    private String staticOtp;

    // Temporary storage for verified phone numbers.
    // In production, this should use Redis with a TTL.
    private final Map<String, Boolean> verifiedPhones = new ConcurrentHashMap<>();

    public OtpVerificationResponse sendOtp(String phone) {
        log.info("Sending OTP to phone: {}", phone);

        if (useStaticOtp()) {
            log.info("Using STATIC OTP for phone: {}", phone);
            return OtpVerificationResponse.builder()
                    .success(true)
                    .message("Static OTP generated for testing.")
                    .phone(phone)
                    .verificationStatus("PENDING")
                    .build();
        }

        try {
            if ("mock-sid".equals(verifyServiceSid) || verifyServiceSid.isEmpty()) {
                log.warn("Twilio Verify Service SID not configured. Cannot send real OTP.");
                return OtpVerificationResponse.builder()
                        .success(false)
                        .message("Twilio not fully configured.")
                        .phone(phone)
                        .verificationStatus("FAILED")
                        .build();
            }

            Verification verification = Verification.creator(
                    verifyServiceSid,
                    phone,
                    "sms").create();

            log.info("Twilio verification status: {}", verification.getStatus());

            return OtpVerificationResponse.builder()
                    .success(true)
                    .message("OTP sent successfully.")
                    .phone(phone)
                    .verificationStatus(verification.getStatus().toUpperCase())
                    .build();

        } catch (ApiException e) {
            log.error("Failed to send Twilio OTP: {}", e.getMessage());
            return OtpVerificationResponse.builder()
                    .success(false)
                    .message("Failed to send OTP: " + e.getMessage())
                    .phone(phone)
                    .verificationStatus("FAILED")
                    .build();
        }
    }

    public OtpVerificationResponse verifyOtp(String phone, String otp) {
        log.info("Verifying OTP for phone: {}", phone);

        if (useStaticOtp()) {
            if (staticOtp.equals(otp)) {
                log.info("Static OTP verified for phone: {}", phone);
                markPhoneAsVerified(phone);
                return OtpVerificationResponse.builder()
                        .success(true)
                        .message("Static OTP verified successfully.")
                        .phone(phone)
                        .verificationStatus("APPROVED")
                        .build();
            } else {
                return OtpVerificationResponse.builder()
                        .success(false)
                        .message("Invalid static OTP.")
                        .phone(phone)
                        .verificationStatus("FAILED")
                        .build();
            }
        }

        try {
            if ("mock-sid".equals(verifyServiceSid) || verifyServiceSid.isEmpty()) {
                return OtpVerificationResponse.builder()
                        .success(false)
                        .message("Twilio not fully configured.")
                        .phone(phone)
                        .verificationStatus("FAILED")
                        .build();
            }

            VerificationCheck verificationCheck = VerificationCheck.creator(
                    verifyServiceSid)
                    .setTo(phone)
                    .setCode(otp)
                    .create();

            boolean isApproved = "approved".equalsIgnoreCase(verificationCheck.getStatus());

            if (isApproved) {
                markPhoneAsVerified(phone);
            }

            return OtpVerificationResponse.builder()
                    .success(isApproved)
                    .message(isApproved ? "Phone verified successfully." : "Invalid or expired OTP.")
                    .phone(phone)
                    .verificationStatus(verificationCheck.getStatus().toUpperCase())
                    .build();

        } catch (ApiException e) {
            log.error("Failed to verify Twilio OTP: {}", e.getMessage());
            return OtpVerificationResponse.builder()
                    .success(false)
                    .message("Failed to verify OTP: " + e.getMessage())
                    .phone(phone)
                    .verificationStatus("FAILED")
                    .build();
        }
    }

    private boolean useStaticOtp() {
        return staticOtp != null && !staticOtp.trim().isEmpty();
    }

    private void markPhoneAsVerified(String phone) {
        verifiedPhones.put(phone, true);
        log.debug("Marked phone as verified: {}", phone);
    }

    public boolean isPhoneVerified(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return verifiedPhones.getOrDefault(phone, false);
    }

    public void clearVerification(String phone) {
        if (phone != null) {
            verifiedPhones.remove(phone);
        }
    }
}
