package com.logistics.order.service;

import com.logistics.order.model.Order;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedEpodService {

    /**
     * Executes the Advanced Electronic Proof of Delivery flow.
     * Incorporates Biometric signature validation (mock) and Blockchain-style
     * cryptographic hashing for the audit ledger.
     */
    public EpodRecord captureProofOfDelivery(Order order, byte[] userSignature, String biometricToken) {
        log.info("Capturing Advanced e-POD for Order {}", order.getOrderId());

        boolean biometricVerified = verifyBiometric(biometricToken);
        if (!biometricVerified) {
            log.warn("Biometric verification failed for order {}", order.getOrderId());
            throw new IllegalArgumentException("Biometric Verification Failed. Delivery cannot be completed.");
        }

        // Generate an immutable hash of the delivery event (simulating a private
        // blockchain ledger entry)
        String deliveryHash = generateLedgerHash(order, biometricToken);

        log.info("e-POD successfully captured and sealed with hash: {}", deliveryHash);

        return EpodRecord.builder()
                .epodId(UUID.randomUUID().toString())
                .orderId(order.getOrderId())
                .driverId(order.getDriverId())
                .timestamp(LocalDateTime.now())
                .biometricVerified(true)
                .cryptographicHash(deliveryHash)
                .build();
    }

    private boolean verifyBiometric(String biometricToken) {
        // Mocking integration with biometric Auth provider (e.g., Apple FaceID SDK,
        // device keystore)
        return biometricToken != null && !biometricToken.isBlank();
    }

    private String generateLedgerHash(Order order, String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String rawData = order.getOrderId() + "-" + order.getDriverId() + "-" + LocalDateTime.now() + "-" + token;
            byte[] hash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            log.error("Failed to generate ledger hash", e);
            return UUID.randomUUID().toString(); // Fallback
        }
    }

    @Data
    @Builder
    public static class EpodRecord {
        private final String epodId;
        private final String orderId;
        private final String driverId;
        private final LocalDateTime timestamp;
        private final Boolean biometricVerified;
        private final String cryptographicHash;
    }
}
