package com.logistics.driver.repository;

import com.logistics.driver.model.DriverShift;
import com.logistics.driver.model.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverShiftRepository extends JpaRepository<DriverShift, Long> {
    
    List<DriverShift> findByDriverId(Long driverId);
    
    List<DriverShift> findByDriverIdAndStatus(Long driverId, ShiftStatus status);
    
    @Query("SELECT s FROM DriverShift s WHERE s.driverId = :driverId AND s.status = :status ORDER BY s.shiftStart DESC")
    Optional<DriverShift> findLatestShiftByStatus(Long driverId, ShiftStatus status);
    
    @Query("SELECT s FROM DriverShift s WHERE s.driverId = :driverId AND s.shiftStart BETWEEN :startDate AND :endDate")
    List<DriverShift> findByDriverIdAndDateRange(Long driverId, LocalDateTime startDate, LocalDateTime endDate);
}
