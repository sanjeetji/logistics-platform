package com.logistics.pricing.service.strategy;

import com.logistics.pricing.dto.PricingDTOs.CalculatedPrice;
import com.logistics.pricing.dto.PricingDTOs.PriceRequest;
import com.logistics.pricing.model.RateCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StandardPricingStrategyTest {

    private StandardPricingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new StandardPricingStrategy();
    }

    @Test
    void calculate_ShouldComputeCorrectly_WhenNoSurge() {
        // Arrange
        RateCard rateCard = RateCard.builder()
                .basePrice(new BigDecimal("50.00"))
                .pricePerKm(new BigDecimal("10.00"))
                .pricePerMinute(new BigDecimal("2.00"))
                .minimumPrice(new BigDecimal("100.00"))
                .build();

        PriceRequest request = PriceRequest.builder()
                .distanceKm(5.0)
                .estimatedTimeMinutes(10.0)
                .isSurgeActive(false)
                .build();

        // 50 + (5 * 10) + (10 * 2) = 50 + 50 + 20 = 120
        
        // Act
        CalculatedPrice result = strategy.calculate(request, rateCard);

        // Assert
        assertEquals(new BigDecimal("120.00"), result.getTotalPrice());
        assertEquals(new BigDecimal("50.00"), result.getBasePrice());
    }

    @Test
    void calculate_ShouldApplyMinimumPrice_WhenTotalIsLower() {
        // Arrange
        RateCard rateCard = RateCard.builder()
                .basePrice(new BigDecimal("50.00"))
                .pricePerKm(new BigDecimal("2.00"))
                .pricePerMinute(new BigDecimal("1.00"))
                .minimumPrice(new BigDecimal("100.00"))
                .build();

        PriceRequest request = PriceRequest.builder()
                .distanceKm(5.0)
                .estimatedTimeMinutes(10.0)
                .isSurgeActive(false)
                .build();

        // 50 + (5 * 2) + (10 * 1) = 50 + 10 + 10 = 70. Min is 100.

        // Act
        CalculatedPrice result = strategy.calculate(request, rateCard);

        // Assert
        assertEquals(new BigDecimal("100.00"), result.getTotalPrice());
    }

    @Test
    void calculate_ShouldApplySurge_WhenSurgeIsActive() {
        // Arrange
        RateCard rateCard = RateCard.builder()
                .basePrice(new BigDecimal("100.00"))
                .pricePerKm(new BigDecimal("0.00"))
                .pricePerMinute(new BigDecimal("0.00"))
                .build();

        PriceRequest request = PriceRequest.builder()
                .isSurgeActive(true)
                .build();

        // 100 * 1.5 = 150

        // Act
        CalculatedPrice result = strategy.calculate(request, rateCard);

        // Assert
        assertEquals(new BigDecimal("150.00"), result.getTotalPrice());
    }
}
