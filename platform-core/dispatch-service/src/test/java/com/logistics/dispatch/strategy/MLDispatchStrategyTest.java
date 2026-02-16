package com.logistics.dispatch.strategy;

import com.logistics.dispatch.client.MLServiceClient;
import com.logistics.dispatch.model.DispatchJob;
import com.logistics.platform.common.dto.order.TransportOrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MLDispatchStrategyTest {

    @Mock
    private MLServiceClient mlServiceClient;

    @InjectMocks
    private MLDispatchStrategy strategy;

    private TransportOrderDto orderDto;
    private DispatchJob job;

    @BeforeEach
    void setUp() {
        orderDto = new TransportOrderDto();
        orderDto.setOrderId("ORDER-123");
        orderDto.setPickupLat(28.7041);
        orderDto.setPickupLng(77.1025);

        job = new DispatchJob();
        job.setOrderId("ORDER-123");
    }

    @Test
    void dispatch_SuccessfulMatch() {
        // Arrange
        MLServiceClient.ScoredDriver scoredDriver = new MLServiceClient.ScoredDriver();
        scoredDriver.setDriverId("DRIVER-101");
        scoredDriver.setScore(95.00);

        MLServiceClient.DriverMatchingResponse response = new MLServiceClient.DriverMatchingResponse();
        response.setOrderId("ORDER-123");
        response.setRankedDrivers(List.of(scoredDriver));

        when(mlServiceClient.getDriverMatch(any(MLServiceClient.DriverMatchingRequest.class)))
                .thenReturn(response);

        // Act
        boolean result = strategy.dispatch(orderDto, job);

        // Assert
        assertTrue(result);
        assertEquals("DRIVER-101", job.getMatchedDriverId());
        verify(mlServiceClient).getDriverMatch(any(MLServiceClient.DriverMatchingRequest.class));
    }

    @Test
    void dispatch_NoMatchFound() {
        // Arrange
        MLServiceClient.DriverMatchingResponse response = new MLServiceClient.DriverMatchingResponse();
        response.setOrderId("ORDER-123");
        response.setRankedDrivers(Collections.emptyList());

        when(mlServiceClient.getDriverMatch(any(MLServiceClient.DriverMatchingRequest.class)))
                .thenReturn(response);

        // Act
        boolean result = strategy.dispatch(orderDto, job);

        // Assert
        assertFalse(result);
        verify(mlServiceClient).getDriverMatch(any(MLServiceClient.DriverMatchingRequest.class));
    }

    @Test
    void dispatch_ServiceError() {
        // Arrange
        when(mlServiceClient.getDriverMatch(any(MLServiceClient.DriverMatchingRequest.class)))
                .thenThrow(new RuntimeException("Service down"));

        // Act
        boolean result = strategy.dispatch(orderDto, job);

        // Assert
        assertFalse(result);
    }
}
