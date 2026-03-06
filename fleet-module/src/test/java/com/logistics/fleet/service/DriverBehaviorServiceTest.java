package com.logistics.fleet.service;

import com.logistics.fleet.dto.DriverBehaviorEventDto;
import com.logistics.fleet.model.Driver;
import com.logistics.fleet.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverBehaviorServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverPerformanceService driverPerformanceService;

    @InjectMocks
    private DriverBehaviorService driverBehaviorService;

    @Captor
    private ArgumentCaptor<Driver> driverCaptor;

    private Driver mockDriver;

    @BeforeEach
    void setUp() {
        mockDriver = Driver.builder()
                .externalId("drv-123")
                .performanceScore(100.0)
                .build();
    }

    @Test
    void testProcessBehaviorEvent_Speeding() {
        // Arrange
        when(driverRepository.findAll()).thenReturn(List.of(mockDriver));

        DriverBehaviorEventDto event = DriverBehaviorEventDto.builder()
                .driverExternalId("drv-123")
                .eventType(DriverBehaviorEventDto.BehaviorEventType.SPEEDING)
                .severity(1.0)
                .build();

        // Act
        driverBehaviorService.processBehaviorEvent(event);

        // Assert
        verify(driverRepository).save(driverCaptor.capture());
        verify(driverPerformanceService).updatePerformanceScore(eq("drv-123"), eq(false));

        Driver savedDriver = driverCaptor.getValue();
        // Speeding base penalty is 5.0 * severity 1.0 = 5.0 penalty => 95.0 new score
        assertEquals(95.0, savedDriver.getPerformanceScore());
    }

    @Test
    void testProcessBehaviorEvent_PhoneUsage_HighSeverity() {
        // Arrange
        mockDriver.setPerformanceScore(80.0);
        when(driverRepository.findAll()).thenReturn(List.of(mockDriver));

        DriverBehaviorEventDto event = DriverBehaviorEventDto.builder()
                .driverExternalId("drv-123")
                .eventType(DriverBehaviorEventDto.BehaviorEventType.PHONE_USAGE)
                .severity(1.5) // E.g., prolonged usage
                .build();

        // Act
        driverBehaviorService.processBehaviorEvent(event);

        // Assert
        verify(driverRepository).save(driverCaptor.capture());

        Driver savedDriver = driverCaptor.getValue();
        // Phone usage base 10.0 * severity 1.5 = 15.0 penalty => 65.0 new score
        assertEquals(65.0, savedDriver.getPerformanceScore());
    }

    @Test
    void testProcessBehaviorEvent_DriverNotFound() {
        // Arrange
        when(driverRepository.findAll()).thenReturn(List.of(mockDriver));

        DriverBehaviorEventDto event = DriverBehaviorEventDto.builder()
                .driverExternalId("unknown-drv")
                .eventType(DriverBehaviorEventDto.BehaviorEventType.SPEEDING)
                .build();

        // Act
        driverBehaviorService.processBehaviorEvent(event);

        // Assert
        verify(driverRepository, never()).save(any(Driver.class));
        verify(driverPerformanceService, never()).updatePerformanceScore(anyString(), anyBoolean());
    }
}
