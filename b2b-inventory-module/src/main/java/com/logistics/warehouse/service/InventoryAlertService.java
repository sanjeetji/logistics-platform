package com.logistics.warehouse.service;

import com.logistics.warehouse.model.InventoryAlert;
import com.logistics.warehouse.model.InventoryItem;
import com.logistics.warehouse.repository.InventoryAlertRepository;
import com.logistics.warehouse.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryAlertService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryAlertRepository inventoryAlertRepository;

    /**
     * Scheduled job to check for low stock items
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkLowStockAlerts() {
        log.info("Running low stock alert check");

        List<InventoryItem> lowStockItems = inventoryItemRepository.findAllLowStockItems();

        for (InventoryItem item : lowStockItems) {
            // Check if alert already exists
            boolean alertExists = inventoryAlertRepository
                    .existsByInventoryItemIdAndAlertTypeAndAcknowledgedFalse(
                            item.getId(),
                            InventoryAlert.AlertType.LOW_STOCK);

            if (!alertExists) {
                InventoryAlert alert = InventoryAlert.builder()
                        .inventoryItemId(item.getId())
                        .productSku(item.getSku())
                        .alertType(InventoryAlert.AlertType.LOW_STOCK)
                        .currentQuantity(item.getQuantity())
                        .thresholdQuantity(item.getReorderLevel())
                        .build();

                inventoryAlertRepository.save(Objects.requireNonNull(alert, "Alert must not be null"));
                log.warn("Low stock alert created for SKU: {}, Current: {}, Threshold: {}",
                        item.getSku(), item.getQuantity(), item.getReorderLevel());
            }
        }
    }

    public List<InventoryAlert> getUnacknowledgedAlerts() {
        return inventoryAlertRepository.findByAcknowledgedFalse();
    }

    @Transactional
    public void acknowledgeAlert(Long alertId, String acknowledgedBy) {
        if (alertId == null) {
            throw new IllegalArgumentException("Alert ID must not be null");
        }
        InventoryAlert alert = inventoryAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(acknowledgedBy);
        alert.setAcknowledgedAt(java.time.LocalDateTime.now());

        inventoryAlertRepository.save(alert);
        log.info("Alert {} acknowledged by {}", alertId, acknowledgedBy);
    }
}
