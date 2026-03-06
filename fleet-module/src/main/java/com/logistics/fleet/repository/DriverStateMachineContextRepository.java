package com.logistics.fleet.repository;

import com.logistics.fleet.model.DriverStateMachineContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverStateMachineContextRepository extends JpaRepository<DriverStateMachineContext, Long> {
    Optional<DriverStateMachineContext> findByDriverEmail(String driverEmail);
}
