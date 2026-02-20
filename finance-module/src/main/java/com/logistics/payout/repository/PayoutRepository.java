package com.logistics.payout.repository;

import com.logistics.payout.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {

    List<Payout> findByDriverId(Long driverId);

    List<Payout> findByStatus(Payout.PayoutStatus status);

    List<Payout> findByGeneratedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payout> findByDriverIdAndStatus(Long driverId, Payout.PayoutStatus status);
}
