package com.logistics.notification.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email provider using Spring Mail
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProvider {

    private final JavaMailSender mailSender;

    /**
     * Send email
     */
    public boolean sendEmail(String to, String subject, String body) {
        log.info("Sending email to {}: {}", to, subject);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            log.info("Email sent successfully");
            return true;
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return false;
        }
    }
}
