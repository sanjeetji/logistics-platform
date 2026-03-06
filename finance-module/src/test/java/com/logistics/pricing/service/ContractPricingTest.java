package com.logistics.pricing.service;

import com.logistics.pricing.model.ContractTerm;
import com.logistics.pricing.model.EnterpriseContract;
import com.logistics.pricing.repository.ContractTermRepository;
import com.logistics.pricing.repository.EnterpriseContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ContractPricingTest {

    @Mock
    private EnterpriseContractRepository contractRepository;

    @Mock
    private ContractTermRepository contractTermRepository;

    @InjectMocks
    private ContractPricingService contractPricingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateContractPrice_withSpecificTerm_shouldReturnOverridePrice() {
        EnterpriseContract contract = EnterpriseContract.builder()
                .id(1L)
                .clientId("CLIENT123")
                .contractName("Acme Corp Q3 Negotiated")
                .isActive(true)
                .build();

        ContractTerm term = ContractTerm.builder()
                .vehicleType("BIKE")
                .baseRate(new BigDecimal("20.00"))
                .perKmRate(new BigDecimal("5.00"))
                .perMinuteRate(new BigDecimal("1.00"))
                .build();

        when(contractRepository.findActiveContractByClientIdAndDate(eq("CLIENT123"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(contract));
        when(contractTermRepository.findByContractIdAndVehicleType(1L, "BIKE"))
                .thenReturn(Optional.of(term));

        Optional<ContractPricingService.ContractPriceResult> resultOpt = contractPricingService
                .calculateContractPrice("CLIENT123", "BIKE", 10.0, 15);

        assertTrue(resultOpt.isPresent());
        ContractPricingService.ContractPriceResult result = resultOpt.get();

        assertTrue(result.isOverride());
        assertEquals("Acme Corp Q3 Negotiated", result.getContractName());
        // 20 (base) + 10 * 5 (dist) + 15 * 1 (time) = 20 + 50 + 15 = 85.00
        assertEquals(0, new BigDecimal("85.00").compareTo(result.getFinalPrice()));
    }

    @Test
    void calculateContractPrice_withGenericDiscount_shouldReturnDiscountPercentage() {
        EnterpriseContract contract = EnterpriseContract.builder()
                .id(2L)
                .clientId("CLIENT456")
                .contractName("Global 15% Off")
                .isActive(true)
                .build();

        ContractTerm genericTerm = ContractTerm.builder()
                .vehicleType("ALL")
                .discountPercentage(new BigDecimal("15.00"))
                .build();

        when(contractRepository.findActiveContractByClientIdAndDate(eq("CLIENT456"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(contract));
        when(contractTermRepository.findByContractIdAndVehicleType(2L, "TRUCK"))
                .thenReturn(Optional.empty()); // No specific term
        when(contractTermRepository.findByContractIdAndVehicleType(2L, "ALL"))
                .thenReturn(Optional.of(genericTerm));

        Optional<ContractPricingService.ContractPriceResult> resultOpt = contractPricingService
                .calculateContractPrice("CLIENT456", "TRUCK", 100.0, 120);

        assertTrue(resultOpt.isPresent());
        ContractPricingService.ContractPriceResult result = resultOpt.get();

        assertFalse(result.isOverride());
        assertEquals("Global 15% Off", result.getContractName());
        assertEquals(0, new BigDecimal("15.00").compareTo(result.getDiscountPercentage()));
        assertNull(result.getFinalPrice());
    }

    @Test
    void calculateContractPrice_noContractFound_shouldReturnEmpty() {
        when(contractRepository.findActiveContractByClientIdAndDate(eq("CLIENT789"), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Optional<ContractPricingService.ContractPriceResult> resultOpt = contractPricingService
                .calculateContractPrice("CLIENT789", "BIKE", 10.0, 15);
        assertFalse(resultOpt.isPresent());
    }
}
