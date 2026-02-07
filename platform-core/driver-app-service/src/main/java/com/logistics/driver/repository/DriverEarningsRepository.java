package com.logistics.driver.repository;

import com.logistics.driver.model.DriverEarnings;
import com.logistics.driver.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverEarningsRepository extends JpaRepository<DriverEarnings, Long> {
    
    Optional<DriverEarnings> findByOrderId(String orderId);
    
    List<DriverEarnings> findByDriverId(Long driverId);
    
    List<DriverEarnings> findByDriverIdAndPaymentStatus(Long driverId, PaymentStatus status);
    
    @Query("SELECT SUM(e.netEarnings) FROM DriverEarnings e WHERE e.driverId = :driverId AND e.paymentStatus = :status")
    BigDecimal getTotalEarningsByStatus(Long driverId, PaymentStatus status);
    
    @Query("SELECT e FROM DriverEarnings e WHERE e.driverId = :driverId AND e.createdAt BETWEEN :startDate AND :endDate")
    List<DriverEarnings> findByDriverIdAndDateRange(Long driverId, LocalDateTime startDate, LocalDateTime endDate);
}
