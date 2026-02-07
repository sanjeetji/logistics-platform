package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    List<Warehouse> findByActive(Boolean active);

    @Query("SELECT w FROM Warehouse w WHERE w.active = true AND w.usedCapacity < w.capacity")
    List<Warehouse> findAvailableWarehouses();

    @Query("SELECT w FROM Warehouse w WHERE w.active = true ORDER BY " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(w.latitude)) * " +
            "cos(radians(w.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(w.latitude))))")
    List<Warehouse> findNearestWarehouses(Double latitude, Double longitude);
}
