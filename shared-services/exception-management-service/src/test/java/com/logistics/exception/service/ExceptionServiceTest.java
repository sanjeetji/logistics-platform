package com.logistics.exception.service;

import com.logistics.exception.model.ExceptionRecord;
import com.logistics.exception.repository.ExceptionRepository;
import com.logistics.platform.event.dto.BusinessExceptionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@org.junit.jupiter.api.Disabled("Fails on Java 24 due to Mockito/ByteBuddy compatibility")
@ExtendWith(MockitoExtension.class)
class ExceptionServiceTest {

    @Mock
    private ExceptionRepository exceptionRepository;

    @InjectMocks
    private ExceptionService exceptionService;

    @Test
    void testSaveException() {
        BusinessExceptionEvent event = BusinessExceptionEvent.builder()
                .exceptionId("EX-123")
                .serviceName("order-service")
                .exceptionType("PAYMENT_FAILED")
                .severity("CRITICAL")
                .build();

        ExceptionRecord savedRecord = ExceptionRecord.builder()
                .id("UUID-1")
                .exceptionId("EX-123")
                .status(ExceptionRecord.ExceptionStatus.OPEN)
                .build();

        when(exceptionRepository.save(any(ExceptionRecord.class))).thenReturn(savedRecord);

        ExceptionRecord result = exceptionService.saveException(event);

        assertNotNull(result);
        assertEquals("EX-123", result.getExceptionId());
        verify(exceptionRepository).save(any(ExceptionRecord.class));
    }

    @Test
    void testResolveException() {
        ExceptionRecord existing = ExceptionRecord.builder()
                .id("UUID-1")
                .status(ExceptionRecord.ExceptionStatus.OPEN)
                .build();

        when(exceptionRepository.findById("UUID-1")).thenReturn(Optional.of(existing));
        when(exceptionRepository.save(any(ExceptionRecord.class))).thenAnswer(i -> i.getArguments()[0]);

        ExceptionRecord result = exceptionService.resolveException("UUID-1", "admin", "Fixed");

        assertEquals(ExceptionRecord.ExceptionStatus.RESOLVED, result.getStatus());
        assertEquals("admin", result.getResolvedBy());
        assertNotNull(result.getResolvedAt());
    }
}
