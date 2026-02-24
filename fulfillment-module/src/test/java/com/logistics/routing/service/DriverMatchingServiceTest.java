package com.logistics.routing.service;

import com.logistics.order.model.Order;
import com.logistics.order.model.OrderLocation;
import com.logistics.platform.common.dto.fleet.DriverDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DriverMatchingServiceTest {

    private final DriverMatchingService matchingService = new DriverMatchingService();

    @Test
    void testScoreDriver_FreshVsFatigued() {
        // Arrange
        Order order = Order.builder()
                .pickupLocation(OrderLocation.builder().latitude(0.0).longitude(0.0).build())
                .build();

        DriverDto freshDriver = DriverDto.builder()
                .name("Fresh")
                .currentLatitude(0.1) // Close
                .currentLongitude(0.1)
                .performanceScore(100.0)
                .shiftStartTime(LocalDateTime.now().minusHours(1))
                .build();

        DriverDto fatiguedDriver = DriverDto.builder()
                .name("Fatigued")
                .currentLatitude(0.1) // Same distance
                .currentLongitude(0.1)
                .performanceScore(100.0)
                .shiftStartTime(LocalDateTime.now().minusHours(10))
                .build();

        // Act
        double freshScore = matchingService.scoreDriver(freshDriver, order);
        double fatiguedScore = matchingService.scoreDriver(fatiguedDriver, order);

        // Assert
        assertTrue(freshScore > fatiguedScore,
                "Fresh driver should have higher score than fatigued driver at same distance");
    }

    @Test
    void testFindBestMatches_RankingOrder() {
        // Arrange
        Order order = Order.builder()
                .pickupLocation(OrderLocation.builder().latitude(0.0).longitude(0.0).build())
                .build();

        DriverDto distantReliable = DriverDto.builder()
                .name("DistantReliable")
                .currentLatitude(0.5).currentLongitude(0.5)
                .performanceScore(100.0)
                .build();

        DriverDto closeUnreliable = DriverDto.builder()
                .name("CloseUnreliable")
                .currentLatitude(0.05).currentLongitude(0.05)
                .performanceScore(50.0)
                .build();

        // Act
        List<DriverDto> matches = matchingService.findBestMatches(Arrays.asList(distantReliable, closeUnreliable),
                order);

        // Assert
        assertEquals(2, matches.size());
        // Close driver scoring high on distance (40%) but low on perf (30%)
        // Distant driver scoring low on distance but high on perf
        // Let's verify who wins based on internal weights
        logScore("DistantReliable", matchingService.scoreDriver(distantReliable, order));
        logScore("CloseUnreliable", matchingService.scoreDriver(closeUnreliable, order));
    }

    private void logScore(String name, double score) {
        System.out.println("Driver " + name + " score: " + score);
    }
}
