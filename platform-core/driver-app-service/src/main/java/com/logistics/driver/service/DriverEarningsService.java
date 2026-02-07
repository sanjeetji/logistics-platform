package com.logistics.driver.service;

import com.logistics.driver.model.DriverEarnings;
import com.logistics.driver.model.PaymentStatus;
import com.logistics.driver.repository.DriverEarningsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for driver earnings management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DriverEarningsService {

    private final DriverEarningsRepository earningsRepository;
    
    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.15"); // 15%
    private static final BigDecimal TAX_PERCENTAGE = new BigDecimal("0.05"); // 5%

    /**
     * Calculate and record earnings for completed order
     */
    @Transactional
    public DriverEarnings recordEarnings(Long driverId, String orderId, BigDecimal orderAmount, Double distanceKm) {
        log.info("Recording earnings for driver: {} on order: {}", driverId, orderId);

        // Calculate earnings breakdown
        BigDecimal baseFare = new BigDecimal("30.00");
        BigDecimal distanceFare = BigDecimal.valueOf(distanceKm).multiply(new BigDecimal("5.00"));
        BigDecimal timeFare = BigDecimal.ZERO; // Can be calculated based on time

        BigDecimal grossEarnings = baseFare.add(distanceFare).add(timeFare);
        
        // Platform fee
        BigDecimal platformFee = grossEarnings.multiply(PLATFORM_FEE_PERCENTAGE);
        
        // Tax
        BigDecimal tax = grossEarnings.multiply(TAX_PERCENTAGE);
        
        // Net earnings
        BigDecimal netEarnings = grossEarnings.subtract(platformFee).subtract(tax);
        netEarnings = netEarnings.setScale(2, RoundingMode.HALF_UP);

        DriverEarnings earnings = DriverEarnings.builder()
                .driverId(driverId)
                .orderId(orderId)
                .baseFare(baseFare)
                .distanceFare(distanceFare)
                .timeFare(timeFare)
                .platformFee(platformFee)
                .tax(tax)
                .netEarnings(netEarnings)
                .build();

        return earningsRepository.save(earnings);
    }

    /**
     * Get driver earnings
     */
    public List<DriverEarnings> getDriverEarnings(Long driverId) {
        return earningsRepository.findByDriverId(driverId);
    }

    /**
     * Get pending earnings
     */
    public BigDecimal getPendingEarnings(Long driverId) {
        BigDecimal pending = earningsRepository.getTotalEarningsByStatus(driverId, PaymentStatus.PENDING);
        return pending != null ? pending : BigDecimal.ZERO;
    }

    /**
     * Get total paid earnings
     */
    public BigDecimal getTotalPaidEarnings(Long driverId) {
        BigDecimal paid = earningsRepository.getTotalEarningsByStatus(driverId, PaymentStatus.PAID);
        return paid != null ? paid : BigDecimal.ZERO;
    }

    /**
     * Get earnings for date range
     */
    public List<DriverEarnings> getEarningsByDateRange(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
        return earningsRepository.findByDriverIdAndDateRange(driverId, startDate, endDate);
    }
}
