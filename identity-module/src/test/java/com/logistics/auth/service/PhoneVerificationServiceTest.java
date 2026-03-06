package com.logistics.auth.service;

import com.logistics.auth.dto.OtpVerificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PhoneVerificationServiceTest {

    @InjectMocks
    private PhoneVerificationService phoneVerificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(phoneVerificationService, "verifyServiceSid", "mock-sid");
        ReflectionTestUtils.setField(phoneVerificationService, "staticOtp", "123456");
    }

    @Test
    void testSendOtp_WithStaticOtp() {
        String phone = "+1234567890";
        OtpVerificationResponse response = phoneVerificationService.sendOtp(phone);

        assertTrue(response.isSuccess());
        assertEquals("PENDING", response.getVerificationStatus());
        assertEquals(phone, response.getPhone());
        assertFalse(phoneVerificationService.isPhoneVerified(phone));
    }

    @Test
    void testVerifyOtp_WithCorrectStaticOtp() {
        String phone = "+1234567890";
        String otp = "123456";

        OtpVerificationResponse response = phoneVerificationService.verifyOtp(phone, otp);

        assertTrue(response.isSuccess());
        assertEquals("APPROVED", response.getVerificationStatus());
        assertTrue(phoneVerificationService.isPhoneVerified(phone));
    }

    @Test
    void testVerifyOtp_WithIncorrectStaticOtp() {
        String phone = "+0987654321";
        String otp = "654321";

        OtpVerificationResponse response = phoneVerificationService.verifyOtp(phone, otp);

        assertFalse(response.isSuccess());
        assertEquals("FAILED", response.getVerificationStatus());
        assertFalse(phoneVerificationService.isPhoneVerified(phone));
    }

    @Test
    void testClearVerification() {
        String phone = "+1122334455";

        // Setup state
        phoneVerificationService.verifyOtp(phone, "123456");
        assertTrue(phoneVerificationService.isPhoneVerified(phone));

        // Action
        phoneVerificationService.clearVerification(phone);

        // Verify
        assertFalse(phoneVerificationService.isPhoneVerified(phone));
    }
}
