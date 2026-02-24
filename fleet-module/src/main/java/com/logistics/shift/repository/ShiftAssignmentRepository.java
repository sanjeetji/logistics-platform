package com.logistics.shift.repository;

import com.logistics.shift.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {

    List<ShiftAssignment> findByDriverIdAndShiftDateBetween(Long driverId, LocalDate startDate, LocalDate endDate);

    boolean existsByDriverIdAndShiftDate(Long driverId, LocalDate shiftDate);

    List<ShiftAssignment> findByShiftDateAndStatus(LocalDate date, ShiftAssignment.ShiftStatus status);

    long countByShiftDate(LocalDate shiftDate);
}
