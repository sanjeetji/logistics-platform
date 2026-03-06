package com.logistics.fleet.repository;

import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>, DriverGeospatialRepository {
        Optional<Driver> findByPhoneNumber(String phoneNumber);

        Optional<Driver> findByEmail(String email);

        List<Driver> findByStatus(DriverStatus status);

        // Projections
        List<com.logistics.fleet.model.projection.DriverSummary> findByStatusAndCurrentOrderIdNotNull(
                        DriverStatus status);

        List<Driver> findByStatusIn(List<DriverStatus> statuses);
}
