package com.logistics.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SMS provider (Mock implementation - replace with actual Twilio integration)
 */
@Component
@Slf4j
public class SmsProvider {

    @Value("${notification.sms.from-number}")
    private String fromNumber;

    /**
     * Send SMS
     */
    public boolean sendSms(String toNumber, String message) {
        log.info("Sending SMS to {}: {}", toNumber, message);
        
        // Mock implementation - replace with actual Twilio API call
        // Example:
        // Message twilioMessage = Message.creator(
        //     new PhoneNumber(toNumber),
        //     new PhoneNumber(fromNumber),
        //     message
        // ).create();
        
        log.info("SMS sent successfully (mock)");
        return true;
    }
}
