package com.logistics.loyalty.service;

import com.logistics.loyalty.model.LoyaltyProfile;
import com.logistics.loyalty.model.PointsTransaction;
import com.logistics.loyalty.repository.LoyaltyProfileRepository;
import com.logistics.loyalty.repository.PointsTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

    @Mock
    private LoyaltyProfileRepository loyaltyProfileRepository;

    @Mock
    private PointsTransactionRepository pointsTransactionRepository;

    @InjectMocks
    private LoyaltyServiceImpl loyaltyService;

    private LoyaltyProfile profile;

    @BeforeEach
    void setUp() {
        profile = LoyaltyProfile.builder()
                .userId("user1")
                .currentPoints(100)
                .totalPointsEarned(100)
                .currentTier(LoyaltyProfile.Tier.BRONZE)
                .build();
        profile.setId(1L);
    }

    @Test
    void earnPoints_Success() {
        when(loyaltyProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));
        when(loyaltyProfileRepository.save(any(LoyaltyProfile.class))).thenAnswer(i -> i.getArgument(0));

        // Order value 100 * 10 points/unit = 1000 points
        loyaltyService.earnPoints("user1", new BigDecimal("100.00"), "order1");

        assertEquals(1100, profile.getCurrentPoints());
        assertEquals(1100, profile.getTotalPointsEarned());
        verify(pointsTransactionRepository).save(any(PointsTransaction.class));
    }

    @Test
    void earnPoints_TierUpgrade() {
        when(loyaltyProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));
        when(loyaltyProfileRepository.save(any(LoyaltyProfile.class))).thenAnswer(i -> i.getArgument(0));

        // Earn 5000 points to cross GOLD threshold (5000)
        // 500 * 10 = 5000
        loyaltyService.earnPoints("user1", new BigDecimal("500.00"), "order2");

        assertEquals(LoyaltyProfile.Tier.GOLD, profile.getCurrentTier());
    }

    @Test
    void redeemPoints_Success() {
        when(loyaltyProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));
        when(loyaltyProfileRepository.save(any(LoyaltyProfile.class))).thenReturn(profile);

        loyaltyService.redeemPoints("user1", 50);

        assertEquals(50, profile.getCurrentPoints());
        verify(pointsTransactionRepository).save(any(PointsTransaction.class));
    }

    @Test
    void redeemPoints_InsufficientBalance() {
        when(loyaltyProfileRepository.findByUserId("user1")).thenReturn(Optional.of(profile));

        assertThrows(IllegalArgumentException.class, () ->
                loyaltyService.redeemPoints("user1", 150));
    }
}
