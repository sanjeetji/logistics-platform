package com.logistics.inventory.service;

import java.util.Objects;

import com.logistics.inventory.model.InventoryItem;
import com.logistics.inventory.repository.InventoryRepository;
import com.logistics.platform.event.dto.InventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String INVENTORY_KEY_PREFIX = "inventory:";

    /**
     * Lua script for atomic stock reservation
     * KEYS[1] = inventory key
     * ARGV[1] = quantity to reserve
     * Returns: 1 if successful, 0 if insufficient stock, -1 if key doesn't exist
     */
    private static final String RESERVE_STOCK_SCRIPT = "local current = redis.call('get', KEYS[1]) " +
            "if not current then return -1 end " +
            "if tonumber(current) >= tonumber(ARGV[1]) then " +
            "  redis.call('decrby', KEYS[1], ARGV[1]) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";

    @SuppressWarnings("null")
    @Transactional
    public InventoryItem initializeStock(@org.springframework.lang.NonNull String sku, String productId,
            String warehouseId, int quantity,
            String location) {
        log.info("Initializing stock for SKU: {}, Quantity: {}", sku, quantity);

        // 1. Save to DB
        InventoryItem item = inventoryRepository.findBySku(sku)
                .map(existing -> {
                    existing.setQuantity(quantity);
                    existing.setWarehouseId(warehouseId);
                    existing.setLocation(location);
                    return existing;
                })
                .orElse(InventoryItem.builder()
                        .sku(sku)
                        .productId(productId)
                        .warehouseId(warehouseId)
                        .quantity(quantity)
                        .reservedQuantity(0)
                        .location(location)
                        .build());

        InventoryItem saved = inventoryRepository.save(item);

        // 2. Sync to Redis
        String key = INVENTORY_KEY_PREFIX + sku;
        redisTemplate.opsForValue().set(key, String.valueOf(quantity));

        // 3. Notify ERP/Other services via Kafka
        publishUpdate(sku, warehouseId, "RESTOCKED", quantity, quantity);

        return saved;
    }

    private void publishUpdate(@org.springframework.lang.NonNull String sku, String warehouseId, String action,
            Integer newQty, Integer delta) {
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .skuId(sku)
                .warehouseId(warehouseId)
                .action(action)
                .newQuantity(newQty)
                .delta(delta)
                .timestamp(LocalDateTime.now())
                .updatedBy("SYSTEM")
                .build();
        kafkaTemplate.send("inventory-updates", sku, event);
    }

    public boolean reserveStock(String sku, int quantity) {
        String key = INVENTORY_KEY_PREFIX + sku;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(RESERVE_STOCK_SCRIPT, Long.class);

        Long result = redisTemplate.execute(script, Objects.requireNonNull(java.util.List.of(key)),
                String.valueOf(quantity));

        if (result == null || result == -1) {
            // Fallback: Check DB if Redis key missing (cache miss or eviction)
            return reserveFromDb(sku, quantity);
        }

        if (result == 1) {
            log.info("Stock reserved in Redis for SKU: {}", sku);
            // Publish event for ERP sync
            Optional<InventoryItem> item = inventoryRepository.findBySku(sku);
            item.ifPresent(
                    i -> publishUpdate(sku, i.getWarehouseId(), "RESERVED", i.getQuantity() - quantity, -quantity));
            return true;
        } else {
            log.warn("Insufficient stock in Redis for SKU: {}", sku);
            return false;
        }
    }

    @Transactional
    public boolean reserveFromDb(String sku, int quantity) {
        // Fallback implementation
        Optional<InventoryItem> itemOpt = inventoryRepository.findBySku(sku);
        if (itemOpt.isPresent()) {
            InventoryItem item = itemOpt.get();
            if (item.getQuantity() != null && item.getQuantity() >= quantity) {
                item.setQuantity(item.getQuantity() - quantity);
                item.setReservedQuantity(item.getReservedQuantity() + quantity);
                inventoryRepository.save(item);

                // Re-populate Redis
                String key = INVENTORY_KEY_PREFIX + sku;
                redisTemplate.opsForValue().set(key, String.valueOf(item.getQuantity().intValue()));

                return true;
            }
        }
        return false;
    }

    public Integer getStock(String sku) {
        String key = INVENTORY_KEY_PREFIX + sku;
        Object val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return Integer.parseInt(val.toString());
        }
        return inventoryRepository.findBySku(sku).map(InventoryItem::getQuantity).orElse(0);
    }
}
