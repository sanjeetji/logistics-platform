package com.logistics.rules.controller;

import com.logistics.platform.common.dto.rules.RuleFacts;
import com.logistics.rules.service.RuleEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules/evaluate")
@RequiredArgsConstructor
public class RuleEvaluationController {

    private final RuleEvaluationService evaluationService;

    @PostMapping("/pricing")
    public ResponseEntity<RuleFacts.PricingFact> evaluatePricing(@RequestBody RuleFacts.PricingFact fact) {
        return ResponseEntity.ok(evaluationService.evaluatePricing(fact));
    }

    @PostMapping("/dispatch")
    public ResponseEntity<RuleFacts.DispatchFact> evaluateDispatch(@RequestBody RuleFacts.DispatchFact fact) {
        return ResponseEntity.ok(evaluationService.evaluateDispatch(fact));
    }

    @PostMapping("/sla")
    public ResponseEntity<RuleFacts.SLAFact> evaluateSla(@RequestBody RuleFacts.SLAFact fact) {
        return ResponseEntity.ok(evaluationService.evaluateSla(fact));
    }
}
