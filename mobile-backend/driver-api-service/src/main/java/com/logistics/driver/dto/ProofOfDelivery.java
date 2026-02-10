package com.logistics.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfDelivery {
    private String orderId;
    private String driverId;
    private LocalDateTime deliveredAt;
    private String recipientName;
    private String recipientSignature; // Base64 encoded image
    private String photoUrl; // URL to delivery photo
    private Double latitude;
    private Double longitude;
    private String notes;
    private Map<String, Object> metadata; // For offline sync tracking
    private Boolean syncedToServer;
}
