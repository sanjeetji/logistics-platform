package com.logistics.routing.kafka;

import com.logistics.order.service.BackorderService;
import com.logistics.platform.event.dto.InventoryUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class InventoryEventConsumerTest {

    @Mock
    private BackorderService backorderService;

    @InjectMocks
    private InventoryEventConsumer inventoryEventConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleInventoryUpdate_Restocked_TriggersRecovery() {
        // Arrange
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .skuId("SKU-123")
                .action("RESTOCKED")
                .build();

        // Act
        inventoryEventConsumer.handleInventoryUpdate(event);

        // Assert
        verify(backorderService, times(1)).recoverOrdersBySku("SKU-123");
    }

    @Test
    void testHandleInventoryUpdate_AdjustedPositive_TriggersRecovery() {
        // Arrange
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .skuId("SKU-456")
                .action("ADJUSTED")
                .delta(10)
                .build();

        // Act
        inventoryEventConsumer.handleInventoryUpdate(event);

        // Assert
        verify(backorderService, times(1)).recoverOrdersBySku("SKU-456");
    }

    @Test
    void testHandleInventoryUpdate_AdjustedNegative_DoesNotTriggerRecovery() {
        // Arrange
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .skuId("SKU-789")
                .action("ADJUSTED")
                .delta(-5)
                .build();

        // Act
        inventoryEventConsumer.handleInventoryUpdate(event);

        // Assert
        verify(backorderService, never()).recoverOrdersBySku(anyString());
    }

    @Test
    void testHandleInventoryUpdate_OtherAction_DoesNotTriggerRecovery() {
        // Arrange
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .skuId("SKU-000")
                .action("RESERVED")
                .build();

        // Act
        inventoryEventConsumer.handleInventoryUpdate(event);

        // Assert
        verify(backorderService, never()).recoverOrdersBySku(anyString());
    }
}
