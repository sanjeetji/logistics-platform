package com.logistics.shift.repository;

import com.logistics.shift.entity.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, Long> {
    
    List<ShiftSwapRequest> findByTargetDriverIdAndStatus(Long targetDriverId, ShiftSwapRequest.SwapStatus status);
    
    List<ShiftSwapRequest> findByRequestingDriverId(Long requestingDriverId);
}
