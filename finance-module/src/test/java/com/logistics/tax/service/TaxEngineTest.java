package com.logistics.tax.service;

import com.logistics.tax.entity.TaxRule;
import com.logistics.tax.repository.TaxRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TaxEngineTest {

    @Mock
    private TaxRuleRepository taxRuleRepository;

    @InjectMocks
    private TaxCalculationService taxCalculationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateTax_withValidUKRule_shouldCalculateVAT20() {
        TaxRule ukRule = TaxRule.builder()
                .countryCode("UK")
                .taxName("VAT")
                .ratePercentage(new BigDecimal("20.00"))
                .isActive(true)
                .build();

        when(taxRuleRepository.findByCountryCodeAndIsActiveTrue("UK"))
                .thenReturn(Optional.of(ukRule));

        BigDecimal subtotal = new BigDecimal("100.00");
        TaxCalculationService.TaxResult result = taxCalculationService.calculateTax(subtotal, "UK");

        assertEquals("VAT (20%)", result.getTaxName());
        assertEquals(0, new BigDecimal("0.2000").compareTo(result.getRate()));
        assertEquals(0, new BigDecimal("20.00").compareTo(result.getTaxAmount()));
        assertEquals(0, new BigDecimal("120.00").compareTo(result.getTotalAmount()));
    }

    @Test
    void calculateTax_withValidINRule_shouldCalculateGST18() {
        TaxRule inRule = TaxRule.builder()
                .countryCode("IN")
                .taxName("GST")
                .ratePercentage(new BigDecimal("18.00"))
                .isActive(true)
                .build();

        when(taxRuleRepository.findByCountryCodeAndIsActiveTrue("IN"))
                .thenReturn(Optional.of(inRule));

        BigDecimal subtotal = new BigDecimal("500.00");
        TaxCalculationService.TaxResult result = taxCalculationService.calculateTax(subtotal, "IN");

        assertEquals("GST (18%)", result.getTaxName());
        assertEquals(0, new BigDecimal("0.1800").compareTo(result.getRate()));
        assertEquals(0, new BigDecimal("90.00").compareTo(result.getTaxAmount()));
        assertEquals(0, new BigDecimal("590.00").compareTo(result.getTotalAmount()));
    }

    @Test
    void calculateTax_withNoRuleFound_shouldFallbackTo10Percent() {
        when(taxRuleRepository.findByCountryCodeAndIsActiveTrue("US"))
                .thenReturn(Optional.empty());

        BigDecimal subtotal = new BigDecimal("100.00");
        TaxCalculationService.TaxResult result = taxCalculationService.calculateTax(subtotal, "US");

        assertEquals("Standard Tax (10%)", result.getTaxName());
        assertEquals(0, new BigDecimal("0.10").compareTo(result.getRate()));
        assertEquals(0, new BigDecimal("10.00").compareTo(result.getTaxAmount()));
        assertEquals(0, new BigDecimal("110.00").compareTo(result.getTotalAmount()));
    }

    @Test
    void calculateTax_withZeroSubtotal_shouldReturnZeros() {
        TaxCalculationService.TaxResult result = taxCalculationService.calculateTax(BigDecimal.ZERO, "UK");

        assertEquals("Tax", result.getTaxName());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getRate()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTaxAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalAmount()));
    }
}
