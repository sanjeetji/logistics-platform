package com.logistics.b2b.repository;

import com.logistics.b2b.model.B2BOrder;
import com.logistics.b2b.model.B2BOrderStatus;
import com.logistics.b2b.model.SLAStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface B2BOrderRepository extends JpaRepository<B2BOrder, Long> {
    
    Optional<B2BOrder> findByOrderId(String orderId);
    
    List<B2BOrder> findByClientId(Long clientId);
    
    List<B2BOrder> findByStatus(B2BOrderStatus status);
    
    List<B2BOrder> findBySlaStatus(SLAStatus slaStatus);
    
    @Query("SELECT o FROM B2BOrder o WHERE o.slaDeadline BETWEEN :startDate AND :endDate")
    List<B2BOrder> findBySlaDead lineRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT o FROM B2BOrder o WHERE o.clientId = :clientId AND o.status = :status")
    List<B2BOrder> findByClientIdAndStatus(Long clientId, B2BOrderStatus status);
}
