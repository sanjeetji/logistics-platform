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

    /**
     * Calculates Cross-Border Customs Duty if origin and destination countries
     * differ.
     * In a full implementation, this would call an external Global Trade API or our
     * ML-Service
     * to classify HS codes. This is the fallback heuristic method.
     */
    public DutyResult calculateCrossBorderDuty(BigDecimal subtotal, String originCountry, String destCountry) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return new DutyResult("Customs Duty", BigDecimal.ZERO, BigDecimal.ZERO, subtotal);
        }

        if (originCountry == null || destCountry == null || originCountry.equalsIgnoreCase(destCountry)) {
            // Domestic, no duty
            return new DutyResult("No Duty (Domestic)", BigDecimal.ZERO, BigDecimal.ZERO, subtotal);
        }

        // Fallback Heuristic: Flat 12% standard duty for international if ML
        // classification is unavailable
        BigDecimal dutyRate = new BigDecimal("0.12");

        // Example of specific corridor rules
        if (originCountry.equalsIgnoreCase("US") && destCountry.equalsIgnoreCase("CA") ||
                originCountry.equalsIgnoreCase("CA") && destCountry.equalsIgnoreCase("US")) {
            // USMCA/NAFTA corridor - reduced duty
            dutyRate = new BigDecimal("0.02");
        } else if (originCountry.equalsIgnoreCase("CN") && destCountry.equalsIgnoreCase("US")) {
            // Example: High tariff corridor fallback
            dutyRate = new BigDecimal("0.25");
        }

        BigDecimal dutyAmount = subtotal.multiply(dutyRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(dutyAmount);

        String formattedName = String.format("Import Duty (%s%%)",
                dutyRate.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());

        return new DutyResult(formattedName, dutyRate, dutyAmount, totalAmount);
    }

    @Data
    @Builder
    public static class TaxResult {
        private final String taxName;
        private final BigDecimal rate;
        private final BigDecimal taxAmount;
        private final BigDecimal totalAmount;
    }

    @Data
    @Builder
    public static class DutyResult {
        private final String dutyName;
        private final BigDecimal rate;
        private final BigDecimal dutyAmount;
        private final BigDecimal totalAmount;
    }
}
