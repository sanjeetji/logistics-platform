package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.WarehouseBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseBinRepository extends JpaRepository<WarehouseBin, Long> {

    Optional<WarehouseBin> findByBinCode(String binCode);

    List<WarehouseBin> findByWarehouseIdAndZone(Long warehouseId, String zone);

    @Query("SELECT b FROM WarehouseBin b WHERE b.warehouseId = :warehouseId " +
            "AND b.active = true AND (b.capacity IS NULL OR b.currentOccupancy < b.capacity)")
    List<WarehouseBin> findAvailableBins(Long warehouseId);
}
