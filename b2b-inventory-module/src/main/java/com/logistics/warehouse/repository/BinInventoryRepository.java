package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.BinInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinInventoryRepository extends JpaRepository<BinInventory, Long> {
    Optional<BinInventory> findByBinIdAndInventoryItemId(Long binId, Long inventoryItemId);

    List<BinInventory> findByInventoryItemId(Long inventoryItemId);

    List<BinInventory> findByBinId(Long binId);
}
