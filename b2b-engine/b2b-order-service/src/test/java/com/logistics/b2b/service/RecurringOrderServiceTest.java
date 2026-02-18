package com.logistics.b2b.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.b2b.dto.CreateB2BOrderRequest;
import com.logistics.b2b.model.RecurringFrequency;
import com.logistics.b2b.model.RecurringOrderTemplate;
import com.logistics.b2b.repository.RecurringOrderTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringOrderServiceTest {

    @Mock
    private RecurringOrderTemplateRepository templateRepository;
    @Mock
    private B2BOrderService orderService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecurringOrderService recurringOrderService;

    @Test
    void testProcessRecurringOrders_Daily() {
        RecurringOrderTemplate template = new RecurringOrderTemplate();
        template.setTemplateId("TMPL-DAILY");
        template.setClientId(1L);
        template.setActive(true);
        template.setStartDate(LocalDate.now().minusDays(1));
        template.setFrequency(RecurringFrequency.DAILY);
        template.setOrderTemplate(Map.of("key", "value"));

        when(templateRepository.findByActive(true)).thenReturn(Collections.singletonList(template));
        when(objectMapper.convertValue(any(), eq(CreateB2BOrderRequest.class))).thenReturn(new CreateB2BOrderRequest());

        recurringOrderService.processRecurringOrders();

        verify(orderService).createOrder(any(CreateB2BOrderRequest.class));
    }

    @Test
    void testProcessRecurringOrders_Weekly_Match() {
        RecurringOrderTemplate template = new RecurringOrderTemplate();
        template.setTemplateId("TMPL-WEEKLY");
        template.setClientId(1L);
        template.setActive(true);
        template.setStartDate(LocalDate.now().minusDays(1));
        template.setFrequency(RecurringFrequency.WEEKLY);
        template.setDayOfWeek(LocalDate.now().getDayOfWeek().getValue());
        template.setOrderTemplate(Map.of("key", "value"));

        when(templateRepository.findByActive(true)).thenReturn(Collections.singletonList(template));
        when(objectMapper.convertValue(any(), eq(CreateB2BOrderRequest.class))).thenReturn(new CreateB2BOrderRequest());

        recurringOrderService.processRecurringOrders();

        verify(orderService).createOrder(any(CreateB2BOrderRequest.class));
    }

    @Test
    void testProcessRecurringOrders_Weekly_NoMatch() {
        RecurringOrderTemplate template = new RecurringOrderTemplate();
        template.setTemplateId("TMPL-WEEKLY-MISMATCH");
        template.setClientId(1L);
        template.setActive(true);
        template.setStartDate(LocalDate.now().minusDays(1));
        template.setFrequency(RecurringFrequency.WEEKLY);
        // Set day of week to something different than today
        template.setDayOfWeek(LocalDate.now().plusDays(1).getDayOfWeek().getValue());
        template.setOrderTemplate(Map.of("key", "value"));

        when(templateRepository.findByActive(true)).thenReturn(Collections.singletonList(template));

        recurringOrderService.processRecurringOrders();

        verify(orderService, never()).createOrder(any());
    }

    @Test
    void testProcessRecurringOrders_Expired() {
        RecurringOrderTemplate template = new RecurringOrderTemplate();
        template.setTemplateId("TMPL-EXPIRED");
        template.setClientId(1L);
        template.setActive(true);
        template.setStartDate(LocalDate.now().minusDays(10));
        template.setEndDate(LocalDate.now().minusDays(1));
        template.setFrequency(RecurringFrequency.DAILY);

        when(templateRepository.findByActive(true)).thenReturn(Collections.singletonList(template));

        recurringOrderService.processRecurringOrders();

        verify(orderService, never()).createOrder(any());
    }
}
