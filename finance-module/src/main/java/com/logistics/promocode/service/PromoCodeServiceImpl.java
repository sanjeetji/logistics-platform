package com.logistics.promocode.service;

import com.logistics.promocode.dto.PromoCodeDTO;
import com.logistics.promocode.model.PromoCode;
import com.logistics.promocode.model.PromoUsage;
import com.logistics.promocode.repository.PromoCodeRepository;
import com.logistics.promocode.repository.PromoUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoUsageRepository promoUsageRepository;

    @Override
    @Transactional
    public PromoCode createPromoCode(PromoCodeDTO dto) {
        if (promoCodeRepository.findByCode(dto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Promo code already exists: " + dto.getCode());
        }

        PromoCode promoCode = PromoCode.builder()
                .code(dto.getCode())
                .description(dto.getDescription())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .minOrderValue(dto.getMinOrderValue())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .validFrom(dto.getValidFrom())
                .validTo(dto.getValidTo())
                .usageLimit(dto.getUsageLimit())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        return promoCodeRepository.save(promoCode);
    }

    @Override
    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }

    @Override
    public PromoCode getPromoCodeByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code: " + code));
    }

    @Override
    public BigDecimal validatePromoCode(String code, String userId, BigDecimal orderValue) {
        PromoCode promo = getPromoCodeByCode(code);
        validateRules(promo, userId, orderValue);
        return calculateDiscount(promo, orderValue);
    }

    @Override
    @Transactional
    public BigDecimal applyPromoCode(String code, String userId, String orderId, BigDecimal orderValue) {
        PromoCode promo = getPromoCodeByCode(code);
        validateRules(promo, userId, orderValue);

        BigDecimal discount = calculateDiscount(promo, orderValue);

        // Record usage
        PromoUsage usage = PromoUsage.builder()
                .promoCodeId(promo.getId())
                .userId(userId)
                .orderId(orderId)
                .discountAmount(discount)
                .usedAt(LocalDateTime.now())
                .build();
        promoUsageRepository.save(usage);

        // Update count
        promo.setUsageCount(promo.getUsageCount() + 1);
        promoCodeRepository.save(promo);

        return discount;
    }

    private void validateRules(PromoCode promo, String userId, BigDecimal orderValue) {
        if (!promo.getActive()) {
            throw new IllegalArgumentException("Promo code is inactive");
        }

        LocalDateTime now = LocalDateTime.now();
        if ((promo.getValidFrom() != null && now.isBefore(promo.getValidFrom())) ||
            (promo.getValidTo() != null && now.isAfter(promo.getValidTo()))) {
            throw new IllegalArgumentException("Promo code is expired or not yet valid");
        }

        if (promo.getUsageLimit() != null && promo.getUsageCount() >= promo.getUsageLimit()) {
            throw new IllegalArgumentException("Promo code usage limit exceeded");
        }

        if (promo.getMinOrderValue() != null && orderValue.compareTo(promo.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Order value is less than minimum required: " + promo.getMinOrderValue());
        }
        
        // Optional: Check if user has already used this code? 
        // Logic can be added here if needed, usually some codes are one-time per user.
    }

    private BigDecimal calculateDiscount(PromoCode promo, BigDecimal orderValue) {
        BigDecimal discount = BigDecimal.ZERO;

        if (promo.getDiscountType() == PromoCode.DiscountType.FLAT) {
            discount = promo.getDiscountValue();
        } else if (promo.getDiscountType() == PromoCode.DiscountType.PERCENTAGE) {
            discount = orderValue.multiply(promo.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            if (promo.getMaxDiscountAmount() != null && discount.compareTo(promo.getMaxDiscountAmount()) > 0) {
                discount = promo.getMaxDiscountAmount();
            }
        }

        // Ensure discount doesn't exceed order value
        if (discount.compareTo(orderValue) > 0) {
            discount = orderValue;
        }

        return discount;
    }
}
