package com.logistics.yard.service;

import com.logistics.dispatch.service.DispatchService;
import com.logistics.platform.dto.warehouse.WarehouseOrderPackedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class YardManagementServiceTest {

    @Mock
    private DispatchService dispatchService;

    @InjectMocks
    private YardManagementService yardManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduleDockAndDispatch() {
        WarehouseOrderPackedEvent event = WarehouseOrderPackedEvent.builder()
                .orderId("ORD100")
                .warehouseId(1L)
                .packedAt(LocalDateTime.now())
                .build();

        yardManagementService.scheduleDockAndDispatch(event);

        // Verify the dispatcher is notified to retrieve this item
        verify(dispatchService, times(1)).initiateDispatch("ORD100");
    }
}
