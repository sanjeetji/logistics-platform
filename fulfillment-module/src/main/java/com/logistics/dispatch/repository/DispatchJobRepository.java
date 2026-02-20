package com.logistics.dispatch.repository;

import com.logistics.dispatch.model.DispatchJob;
import com.logistics.dispatch.model.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchJobRepository extends JpaRepository<DispatchJob, Long> {
    Optional<DispatchJob> findByOrderId(String orderId);

    List<DispatchJob> findByStatus(DispatchStatus status);
}
