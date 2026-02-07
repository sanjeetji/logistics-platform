package com.logistics.compliance.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proof_of_delivery")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProofOfDelivery extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String podId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String recipientName;

    private String recipientSignature; // Base64 encoded or file path

    @Column(nullable = false)
    private LocalDateTime deliveryTime;

    private Double deliveryLatitude;

    private Double deliveryLongitude;

    private String photoUrl; // Photo of delivered goods

    @Column(columnDefinition = "text")
    private String notes;

    @Builder.Default
    @Column(nullable = false)
    private Boolean verified = false;

    private String verifiedBy;

    private LocalDateTime verifiedAt;
}
