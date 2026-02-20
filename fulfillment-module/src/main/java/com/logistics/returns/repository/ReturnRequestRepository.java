package com.logistics.returns.repository;

import com.logistics.returns.model.ReturnRequest;
import com.logistics.returns.model.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    Optional<ReturnRequest> findByReturnId(String returnId);

    List<ReturnRequest> findByOrderId(String orderId);

    List<ReturnRequest> findByCustomerId(String customerId);

    List<ReturnRequest> findByStatus(ReturnStatus status);
}
