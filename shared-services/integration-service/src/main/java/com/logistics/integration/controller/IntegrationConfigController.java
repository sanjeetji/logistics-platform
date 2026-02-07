package com.logistics.integration.controller;

import com.logistics.integration.model.WebhookConfig;
import com.logistics.integration.repository.WebhookConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/integration-config")
@RequiredArgsConstructor
public class IntegrationConfigController {

    private final WebhookConfigRepository configRepository;

    @PostMapping
    public ResponseEntity<WebhookConfig> createConfig(@RequestBody WebhookConfig config) {
        return ResponseEntity.ok(configRepository.save(config));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<WebhookConfig>> getConfigs(@PathVariable String tenantId) {
        // Find by tenant logic would go here, using findAll for now
        return ResponseEntity.ok(configRepository.findAll()); 
    }
}
