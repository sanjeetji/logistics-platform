package com.logistics.tax.service;

import com.logistics.tax.entity.TaxRule;
import com.logistics.tax.repository.TaxRuleRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class TaxCalculationService {

    private final TaxRuleRepository taxRuleRepository;

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.10"); // 10% fallback
    private static final String DEFAULT_TAX_NAME = "Standard Tax";

    public TaxResult calculateTax(BigDecimal subtotal, String countryCode) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return new TaxResult("Tax", BigDecimal.ZERO, BigDecimal.ZERO, subtotal);
        }

        TaxRule rule = null;
        if (countryCode != null && !countryCode.isBlank()) {
            rule = taxRuleRepository.findByCountryCodeAndIsActiveTrue(countryCode).orElse(null);
        }

        BigDecimal rateMultiplier;
        String name;

        if (rule != null) {
            // e.g., 20.00 / 100 = 0.20
            rateMultiplier = rule.getRatePercentage().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            name = rule.getTaxName();
        } else {
            // Fallback
            rateMultiplier = DEFAULT_TAX_RATE;
            name = DEFAULT_TAX_NAME;
        }

        BigDecimal taxAmount = subtotal.multiply(rateMultiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        // Format name for display, e.g., "VAT (20.00%)" or "Standard Tax (10%)"
        String formattedName = String.format("%s (%s%%)", name,
                rateMultiplier.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());

        return new TaxResult(formattedName, rateMultiplier, taxAmount, totalAmount);
    }

    @Data
    @Builder
    public static class TaxResult {
        private final String taxName;
        private final BigDecimal rate;
        private final BigDecimal taxAmount;
        private final BigDecimal totalAmount;
    }
}
