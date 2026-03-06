package com.logistics.fleet.repository;

import com.logistics.fleet.model.Vehicle;
import com.logistics.fleet.model.VehicleStatus;
import com.logistics.fleet.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByActive(boolean active);

    List<Vehicle> findByStatusAndActive(VehicleStatus status, boolean active);

    List<Vehicle> findByTypeAndActive(VehicleType type, boolean active);

    List<Vehicle> findByStatus(VehicleStatus status);
}
