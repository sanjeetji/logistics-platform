package com.logistics.payout.repository;

import com.logistics.payout.model.PayoutRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayoutRequestRepository extends JpaRepository<PayoutRequest, Long> {
    List<PayoutRequest> findByDriverId(String driverId);
    List<PayoutRequest> findByStatus(PayoutRequest.PayoutStatus status);
}
