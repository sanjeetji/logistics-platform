package com.logistics.erp.service;

import com.logistics.erp.adapter.ErpConnector;
import com.logistics.platform.event.dto.InventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SapInventorySyncService {

    private final ErpConnector erpConnector;

    public SapInventorySyncService(@Qualifier("sapConnector") ErpConnector erpConnector) {
        this.erpConnector = erpConnector;
    }

    @KafkaListener(topics = "inventory-updates", groupId = "erp-group")
    public void handleInventoryUpdate(InventoryUpdatedEvent event) {
        log.info("Received inventory update for SKU: {}", event.getSkuId());
        
        try {
            boolean success = erpConnector.syncInventory(
                event.getSkuId(), 
                new BigDecimal(event.getNewQuantity())
            );
            
            if (success) {
                log.info("Successfully synced inventory to SAP for SKU: {}", event.getSkuId());
            } else {
                log.error("Failed to sync inventory to SAP for SKU: {}", event.getSkuId());
            }
        } catch (Exception e) {
            log.error("Error syncing inventory to SAP for SKU: {}", event.getSkuId(), e);
        }
    }
}
