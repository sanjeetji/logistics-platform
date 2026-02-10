package com.logistics.customer.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_points")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPoints extends BaseEntity {

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalPoints = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal availablePoints = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal redeemedPoints = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private LoyaltyTier tier;

    private LocalDateTime tierExpiresAt;

    public enum LoyaltyTier {
        BRONZE(100),
        SILVER(500),
        GOLD(1000),
        PLATINUM(5000);

        private final int pointsRequired;

        LoyaltyTier(int pointsRequired) {
            this.pointsRequired = pointsRequired;
        }

        public int getPointsRequired() {
            return pointsRequired;
        }

        public static LoyaltyTier calculateTier(BigDecimal points) {
            int p = points.intValue();
            if (p >= PLATINUM.pointsRequired) return PLATINUM;
            if (p >= GOLD.pointsRequired) return GOLD;
            if (p >= SILVER.pointsRequired) return SILVER;
            return BRONZE;
        }
    }

    public void addPoints(BigDecimal points) {
        this.totalPoints = this.totalPoints.add(points);
        this.availablePoints = this.availablePoints.add(points);
        this.tier = LoyaltyTier.calculateTier(this.totalPoints);
    }

    public void redeemPoints(BigDecimal points) {
        if (this.availablePoints.compareTo(points) < 0) {
            throw new RuntimeException("Insufficient points");
        }
        this.availablePoints = this.availablePoints.subtract(points);
        this.redeemedPoints = this.redeemedPoints.add(points);
    }
}
