package com.logistics.compliance.service;

import com.logistics.compliance.model.ProofOfDelivery;
import com.logistics.compliance.repository.ProofOfDeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for Proof of Delivery management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProofOfDeliveryService {

    private final ProofOfDeliveryRepository podRepository;

    /**
     * Create POD
     */
    @Transactional
    public ProofOfDelivery createPOD(String orderId, String recipientName, String signature,
            Double latitude, Double longitude, String photoUrl, String notes) {
        log.info("Creating POD for order: {}", orderId);

        ProofOfDelivery pod = ProofOfDelivery.builder()
                .podId("POD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(orderId)
                .recipientName(recipientName)
                .recipientSignature(signature)
                .deliveryTime(LocalDateTime.now())
                .deliveryLatitude(latitude)
                .deliveryLongitude(longitude)
                .photoUrl(photoUrl)
                .notes(notes)
                .verified(false)
                .build();

        return podRepository.save(Objects.requireNonNull(pod, "Pod must not be null"));
    }

    /**
     * Get POD by order ID
     */
    public ProofOfDelivery getPODByOrderId(String orderId) {
        return podRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("POD not found for order: " + orderId));
    }

    /**
     * Verify POD
     */
    @Transactional
    public ProofOfDelivery verifyPOD(String podId, String verifiedBy) {
        ProofOfDelivery pod = podRepository.findByPodId(podId)
                .orElseThrow(() -> new RuntimeException("POD not found: " + podId));

        pod.setVerified(true);
        pod.setVerifiedBy(verifiedBy);
        pod.setVerifiedAt(LocalDateTime.now());

        return podRepository.save(pod);
    }

    /**
     * Get unverified PODs
     */
    public List<ProofOfDelivery> getUnverifiedPODs() {
        return podRepository.findByVerified(false);
    }
}
