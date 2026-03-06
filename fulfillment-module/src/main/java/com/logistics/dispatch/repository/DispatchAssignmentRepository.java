package com.logistics.dispatch.repository;

import com.logistics.dispatch.model.AssignmentStatus;
import com.logistics.dispatch.model.DispatchAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchAssignmentRepository extends JpaRepository<DispatchAssignment, Long> {

    Optional<DispatchAssignment> findByOrderId(String orderId);

    List<DispatchAssignment> findByDriverId(Long driverId);

    List<DispatchAssignment> findByDriverIdAndStatus(Long driverId, AssignmentStatus status);

    List<DispatchAssignment> findByStatus(AssignmentStatus status);

    Optional<DispatchAssignment> findByOrderIdAndStatus(String orderId, AssignmentStatus status);
}
