package com.logistics.promocode.service;

import com.logistics.promocode.dto.PromoCodeDTO;
import com.logistics.promocode.model.PromoCode;
import com.logistics.promocode.repository.PromoCodeRepository;
import com.logistics.promocode.repository.PromoUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private PromoUsageRepository promoUsageRepository;

    @InjectMocks
    private PromoCodeServiceImpl promoCodeService;

    private PromoCode validPromo;

    @BeforeEach
    void setUp() {
        validPromo = PromoCode.builder()
                .code("WELCOME50")
                .discountType(PromoCode.DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("50.00"))
                .minOrderValue(new BigDecimal("100.00"))
                .maxDiscountAmount(new BigDecimal("100.00"))
                .active(true)
                .usageLimit(100)
                .usageCount(0)
                .build();
        validPromo.setId(1L);
    }

    @Test
    void createPromoCode_Success() {
        PromoCodeDTO dto = PromoCodeDTO.builder()
                .code("NEWCODE")
                .discountType(PromoCode.DiscountType.FLAT)
                .discountValue(new BigDecimal("10.00"))
                .build();

        when(promoCodeRepository.findByCode("NEWCODE")).thenReturn(Optional.empty());
        when(promoCodeRepository.save(any(PromoCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PromoCode created = promoCodeService.createPromoCode(dto);

        assertNotNull(created);
        assertEquals("NEWCODE", created.getCode());
        verify(promoCodeRepository).save(any(PromoCode.class));
    }

    @Test
    void validatePromoCode_Success() {
        when(promoCodeRepository.findByCode("WELCOME50")).thenReturn(Optional.of(validPromo));

        BigDecimal discount = promoCodeService.validatePromoCode("WELCOME50", "user1", new BigDecimal("300.00"));

        // 50% of 300 is 150, but max discount is 100
        assertEquals(new BigDecimal("100.00"), discount);
    }

    @Test
    void validatePromoCode_MinOrderValueFail() {
        when(promoCodeRepository.findByCode("WELCOME50")).thenReturn(Optional.of(validPromo));

        assertThrows(IllegalArgumentException.class, () ->
                promoCodeService.validatePromoCode("WELCOME50", "user1", new BigDecimal("50.00")));
    }

    @Test
    void validatePromoCode_Expired() {
        validPromo.setValidTo(LocalDateTime.now().minusDays(1));
        when(promoCodeRepository.findByCode("WELCOME50")).thenReturn(Optional.of(validPromo));

        assertThrows(IllegalArgumentException.class, () ->
                promoCodeService.validatePromoCode("WELCOME50", "user1", new BigDecimal("200.00")));
    }

    @Test
    void validatePromoCode_UsageLimitExceeded() {
        validPromo.setUsageCount(100);
        when(promoCodeRepository.findByCode("WELCOME50")).thenReturn(Optional.of(validPromo));

        assertThrows(IllegalArgumentException.class, () ->
                promoCodeService.validatePromoCode("WELCOME50", "user1", new BigDecimal("200.00")));
    }

    @Test
    void applyPromoCode_Success() {
        when(promoCodeRepository.findByCode("WELCOME50")).thenReturn(Optional.of(validPromo));
        when(promoUsageRepository.save(any())).thenReturn(null);

        BigDecimal discount = promoCodeService.applyPromoCode("WELCOME50", "user1", "order1", new BigDecimal("200.00"));

        assertEquals(new BigDecimal("100.00"), discount);
        verify(promoUsageRepository).save(any());
        assertEquals(1, validPromo.getUsageCount());
    }
}
