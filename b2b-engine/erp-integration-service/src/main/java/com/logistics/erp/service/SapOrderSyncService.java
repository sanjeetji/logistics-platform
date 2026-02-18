package com.logistics.erp.service;

import com.logistics.erp.adapter.ErpConnector;
import com.logistics.erp.client.B2BOrderClient;
import com.logistics.platform.common.dto.order.B2BOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SapOrderSyncService {

    private final ErpConnector erpConnector;
    private final B2BOrderClient b2bOrderClient;

    public SapOrderSyncService(ErpConnector erpConnector, B2BOrderClient b2bOrderClient) {
        this.erpConnector = erpConnector;
        this.b2bOrderClient = b2bOrderClient;
    }

    @Scheduled(cron = "${sap.sync.order-cron}")
    public void syncOrders() {
        log.info("Starting SAP order synchronization...");
        try {
            List<Map<String, Object>> newOrders = erpConnector.fetchNewOrders();
            log.info("Fetched {} new orders from SAP", newOrders.size());

            for (Map<String, Object> sapOrder : newOrders) {
                try {
                    B2BOrderDto dto = mapToDto(sapOrder);
                    b2bOrderClient.syncOrder(dto);
                    log.info("Successfully synced SAP order: {}", dto.getSapOrderId());
                } catch (Exception e) {
                    log.error("Failed to sync SAP order: {}", sapOrder.get("sapOrderId"), e);
                }
            }
        } catch (Exception e) {
            log.error("Error during SAP order synchronization flow", e);
        }
    }

    @SuppressWarnings("unchecked")
    private B2BOrderDto mapToDto(Map<String, Object> sapOrder) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) sapOrder.get("items");

        return B2BOrderDto.builder()
                .sapOrderId((String) sapOrder.get("sapOrderId"))
                .clientId((String) sapOrder.get("clientId"))
                .totalAmount((BigDecimal) sapOrder.get("amount"))
                .status("SAP_SYNCED")
                .items(items.stream().map(item -> B2BOrderDto.B2BOrderItemDto.builder()
                        .sku((String) item.get("sku"))
                        .quantity((Integer) item.get("quantity"))
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
