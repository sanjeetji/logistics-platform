package com.logistics.promocode.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_usage")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoUsage extends BaseEntity {

    @Column(nullable = false)
    private Long promoCodeId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private LocalDateTime usedAt;
}
