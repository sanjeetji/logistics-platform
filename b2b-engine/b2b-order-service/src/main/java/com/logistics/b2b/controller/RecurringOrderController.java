package com.logistics.b2b.controller;

import com.logistics.b2b.model.RecurringOrderTemplate;
import com.logistics.b2b.service.RecurringOrderService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/b2b/recurring")
@RequiredArgsConstructor
public class RecurringOrderController {

    private final RecurringOrderService recurringOrderService;

    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<RecurringOrderTemplate>> createTemplate(
            @RequestBody RecurringOrderTemplate template) {
        RecurringOrderTemplate created = recurringOrderService.createTemplate(template);
        return ResponseEntity.ok(ApiResponse.success(created, "Template created successfully"));
    }

    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<RecurringOrderTemplate>> getTemplate(@PathVariable String templateId) {
        RecurringOrderTemplate template = recurringOrderService.getTemplateById(templateId);
        return ResponseEntity.ok(ApiResponse.success(template));
    }

    @GetMapping("/templates/client/{clientId}")
    public ResponseEntity<ApiResponse<List<RecurringOrderTemplate>>> getClientTemplates(@PathVariable Long clientId) {
        List<RecurringOrderTemplate> templates = recurringOrderService.getClientTemplates(clientId);
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<ApiResponse<Void>> deactivateTemplate(@PathVariable String templateId) {
        recurringOrderService.deactivateTemplate(templateId);
        return ResponseEntity.ok(ApiResponse.success(null, "Template deactivated"));
    }
}
