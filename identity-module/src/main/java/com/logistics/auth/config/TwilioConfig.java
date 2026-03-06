package com.logistics.auth.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.verify-service-sid:}")
    private String verifyServiceSid;

    @PostConstruct
    public void init() {
        if (accountSid == null || accountSid.isEmpty() || accountSid.equals("mock-sid") || authToken == null
                || authToken.isEmpty() || authToken.equals("mock-token")) {
            log.warn(
                    "Twilio credentials are not fully configured or are using mock values. SMS delivery will fail unless STATIC_OTP is used.");
            return;
        }
        log.info("Initializing Twilio client with Account SID: {}",
                accountSid.substring(0, Math.min(accountSid.length(), 6)) + "...");
        Twilio.init(accountSid, authToken);
    }
}
