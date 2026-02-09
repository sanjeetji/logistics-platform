package com.logistics.sla.controller;

import com.logistics.sla.model.SLA;
import com.logistics.sla.repository.SLARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/slas")
@RequiredArgsConstructor
public class SLAController {

    private final SLARepository slaRepository;

    @PostMapping
    public ResponseEntity<SLA> createSLA(@RequestBody SLA sla) {
        return ResponseEntity.ok(slaRepository.save(sla));
    }

    @GetMapping
    public ResponseEntity<List<SLA>> getAllSLAs() {
        return ResponseEntity.ok(slaRepository.findAll());
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<SLA>> getSLAsByEntityType(@PathVariable String entityType) {
        return ResponseEntity.ok(slaRepository.findByEntityTypeAndIsActiveTrue(entityType));
    }
}
