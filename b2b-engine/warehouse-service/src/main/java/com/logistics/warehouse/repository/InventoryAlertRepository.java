package com.logistics.warehouse.repository;

import com.logistics.warehouse.model.InventoryAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAlertRepository extends JpaRepository<InventoryAlert, Long> {

    List<InventoryAlert> findByAcknowledgedFalse();

    boolean existsByInventoryItemIdAndAlertTypeAndAcknowledgedFalse(
            Long inventoryItemId,
            InventoryAlert.AlertType alertType);
}
