package com.logistics.b2b.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.b2b.dto.CreateB2BOrderRequest;
import com.logistics.b2b.model.RecurringFrequency;
import com.logistics.b2b.model.RecurringOrderTemplate;
import com.logistics.b2b.repository.RecurringOrderTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for recurring order management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringOrderService {

    private final RecurringOrderTemplateRepository templateRepository;
    private final B2BOrderService orderService;
    private final ObjectMapper objectMapper;

    /**
     * Create recurring order template
     */
    @Transactional
    public RecurringOrderTemplate createTemplate(RecurringOrderTemplate template) {
        template.setTemplateId("TMPL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return templateRepository.save(template);
    }

    /**
     * Get template by ID
     */
    public RecurringOrderTemplate getTemplateById(String templateId) {
        return templateRepository.findByTemplateId(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found: " + templateId));
    }

    /**
     * Get client templates
     */
    public List<RecurringOrderTemplate> getClientTemplates(Long clientId) {
        return templateRepository.findByClientId(clientId);
    }

    /**
     * Deactivate template
     */
    @Transactional
    public void deactivateTemplate(String templateId) {
        RecurringOrderTemplate template = getTemplateById(templateId);
        template.setActive(false);
        templateRepository.save(template);
    }

    /**
     * Scheduled task to process recurring orders
     * Runs daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processRecurringOrders() {
        log.info("Processing recurring orders...");

        LocalDate today = LocalDate.now();
        List<RecurringOrderTemplate> activeTemplates = templateRepository.findByActive(true);

        int ordersCreated = 0;

        for (RecurringOrderTemplate template : activeTemplates) {
            if (shouldCreateOrder(template, today)) {
                try {
                    createOrderFromTemplate(template);
                    ordersCreated++;
                } catch (Exception e) {
                    log.error("Failed to create order from template {}: {}", template.getTemplateId(), e.getMessage());
                }
            }
        }

        log.info("Recurring orders processed. Created: {}", ordersCreated);
    }

    /**
     * Check if order should be created today
     */
    private boolean shouldCreateOrder(RecurringOrderTemplate template, LocalDate today) {
        // Check if within date range
        if (today.isBefore(template.getStartDate())) {
            return false;
        }
        if (template.getEndDate() != null && today.isAfter(template.getEndDate())) {
            return false;
        }

        return switch (template.getFrequency()) {
            case DAILY -> true;
            case WEEKLY -> template.getDayOfWeek() != null &&
                    today.getDayOfWeek().getValue() == template.getDayOfWeek();
            case MONTHLY -> template.getDayOfMonth() != null &&
                    today.getDayOfMonth() == template.getDayOfMonth();
        };
    }

    /**
     * Create order from template
     */
    private void createOrderFromTemplate(RecurringOrderTemplate template) {
        log.info("Creating order from template: {}", template.getTemplateId());

        try {
            // Extract order details from template
            Map<String, Object> templateData = template.getOrderTemplate();

            // Map JSON template to CreateB2BOrderRequest
            CreateB2BOrderRequest request = objectMapper.convertValue(templateData, CreateB2BOrderRequest.class);

            // Ensure client ID and metadata are correctly set
            request.setClientId(template.getClientId());
            if (request.getMetadata() == null) {
                request.setMetadata(Map.of("templateId", template.getTemplateId()));
            } else {
                request.getMetadata().put("templateId", template.getTemplateId());
            }

            // Create the order
            orderService.createOrder(request);

            log.info("Successfully created B2B order from template: {}", template.getTemplateId());
        } catch (Exception e) {
            log.error("Error transforming template to order request: {}", template.getTemplateId(), e);
            throw new RuntimeException("Failed to generate order from template", e);
        }
    }
}
