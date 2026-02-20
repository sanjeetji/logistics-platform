package com.logistics.erp.controller;

import com.logistics.erp.client.B2BOrderClient;
import com.logistics.platform.common.dto.order.B2BOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/webhooks/oracle")
@RequiredArgsConstructor
@Slf4j
public class OracleWebhookController {

    private final B2BOrderClient b2bOrderClient;

    @PostMapping("/order-update")
    public ResponseEntity<Void> handleOrderUpdate(@RequestBody Map<String, Object> payload) {
        log.info("Received order update webhook from Oracle: {}", payload);
        
        try {
            // Logic to process Oracle-specific webhook payload
            // For now, mapping it and syncing to b2b-order-service
            B2BOrderDto dto = mapPayloadToDto(payload);
            b2bOrderClient.syncOrder(dto);
            log.info("Successfully processed Oracle webhook for order: {}", dto.getSapOrderId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing Oracle webhook", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private B2BOrderDto mapPayloadToDto(Map<String, Object> payload) {
        // Mock payload mapping
        String oracleOrderId = (String) payload.get("order_id");
        String customerId = (String) payload.get("customer_id");
        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
        
        return B2BOrderDto.builder()
                .sapOrderId(oracleOrderId)
                .clientId(customerId)
                .totalAmount(new BigDecimal(payload.get("amount").toString()))
                .status("ORACLE_WEBHOOK_UPDATED")
                .items(items.stream().map(item -> 
                    B2BOrderDto.B2BOrderItemDto.builder()
                        .sku((String) item.get("sku"))
                        .quantity((Integer) item.get("qty"))
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
