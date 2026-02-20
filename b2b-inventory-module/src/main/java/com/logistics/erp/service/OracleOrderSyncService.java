package com.logistics.erp.service;

import com.logistics.erp.adapter.ErpConnector;
import com.logistics.erp.client.B2BOrderClient;
import com.logistics.platform.common.dto.order.B2BOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OracleOrderSyncService {

    private final ErpConnector erpConnector;
    private final B2BOrderClient b2bOrderClient;

    public OracleOrderSyncService(@Qualifier("oracleConnector") ErpConnector erpConnector, B2BOrderClient b2bOrderClient) {
        this.erpConnector = erpConnector;
        this.b2bOrderClient = b2bOrderClient;
    }

    @Scheduled(cron = "${oracle.sync.order-cron:0 0/15 * * * ?}")
    public void syncOrders() {
        log.info("Starting Oracle order synchronization...");
        try {
            List<Map<String, Object>> newOrders = erpConnector.fetchNewOrders();
            log.info("Fetched {} new orders from Oracle", newOrders.size());

            for (Map<String, Object> oracleOrder : newOrders) {
                try {
                    B2BOrderDto dto = mapToDto(oracleOrder);
                    b2bOrderClient.syncOrder(dto);
                    log.info("Successfully synced Oracle order: {}", dto.getSapOrderId());
                } catch (Exception e) {
                    log.error("Failed to sync Oracle order: {}", oracleOrder.get("oracleOrderId"), e);
                }
            }
        } catch (Exception e) {
            log.error("Error during Oracle order synchronization flow", e);
        }
    }

    private B2BOrderDto mapToDto(Map<String, Object> oracleOrder) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) oracleOrder.get("lineItems");
        
        return B2BOrderDto.builder()
                .sapOrderId((String) oracleOrder.get("oracleOrderId")) // Reusing sapOrderId field for simplicity in this version
                .clientId((String) oracleOrder.get("customerNumber"))
                .totalAmount((BigDecimal) oracleOrder.get("totalValue"))
                .status("ORACLE_SYNCED")
                .items(items.stream().map(item -> 
                    B2BOrderDto.B2BOrderItemDto.builder()
                        .sku((String) item.get("partNumber"))
                        .quantity((Integer) item.get("qty"))
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
