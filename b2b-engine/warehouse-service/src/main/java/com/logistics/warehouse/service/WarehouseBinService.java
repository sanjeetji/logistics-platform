package com.logistics.warehouse.service;

import com.logistics.warehouse.model.BinInventory;
import com.logistics.warehouse.model.WarehouseBin;
import com.logistics.warehouse.repository.BinInventoryRepository;
import com.logistics.warehouse.repository.WarehouseBinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseBinService {

    private final WarehouseBinRepository binRepository;
    private final BinInventoryRepository binInventoryRepository;

    /**
     * Find an available bin for a specific warehouse
     */
    public WarehouseBin findAvailableBin(Long warehouseId, WarehouseBin.BinType type) {
        log.info("Finding available bin in warehouse {} of type {}", warehouseId, type);
        List<WarehouseBin> availableBins = binRepository.findAvailableBins(warehouseId);

        return availableBins.stream()
                .filter(b -> type == null || b.getBinType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available bins found in warehouse " + warehouseId));
    }

    /**
     * Allocate stock to a bin
     */
    @Transactional
    public void allocateStockToBin(Long binId, Long inventoryItemId, Integer quantity) {
        log.info("Allocating {} items of inventory {} to bin {}", quantity, inventoryItemId, binId);

        WarehouseBin bin = binRepository.findById(binId)
                .orElseThrow(() -> new RuntimeException("Bin not found: " + binId));

        if (bin.getCapacity() != null && bin.getCurrentOccupancy() + quantity > bin.getCapacity()) {
            throw new RuntimeException("Insufficient capacity in bin: " + bin.getBinCode());
        }

        BinInventory binInventory = binInventoryRepository.findByBinIdAndInventoryItemId(binId, inventoryItemId)
                .orElse(BinInventory.builder()
                        .binId(binId)
                        .inventoryItemId(inventoryItemId)
                        .quantity(0)
                        .reservedQuantity(0)
                        .build());

        binInventory.setQuantity(binInventory.getQuantity() + quantity);
        binInventoryRepository.save(binInventory);

        bin.setCurrentOccupancy(bin.getCurrentOccupancy() + quantity);
        binRepository.save(bin);
    }

    /**
     * Deallocate stock from a bin
     */
    @Transactional
    public void deallocateStockFromBin(Long binId, Long inventoryItemId, Integer quantity) {
        log.info("Deallocating {} items of inventory {} from bin {}", quantity, inventoryItemId, binId);

        BinInventory binInventory = binInventoryRepository.findByBinIdAndInventoryItemId(binId, inventoryItemId)
                .orElseThrow(() -> new RuntimeException("Inventory not found in bin " + binId));

        if (binInventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock in bin for deallocation");
        }

        binInventory.setQuantity(binInventory.getQuantity() - quantity);
        if (binInventory.getQuantity() == 0) {
            binInventoryRepository.delete(binInventory);
        } else {
            binInventoryRepository.save(binInventory);
        }

        WarehouseBin bin = binRepository.findById(binId)
                .orElseThrow(() -> new RuntimeException("Bin not found: " + binId));
        bin.setCurrentOccupancy(Math.max(0, bin.getCurrentOccupancy() - quantity));
        binRepository.save(bin);
    }
}
