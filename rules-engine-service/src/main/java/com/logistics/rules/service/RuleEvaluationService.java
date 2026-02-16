package com.logistics.rules.service;

import com.logistics.platform.common.dto.rules.RuleFacts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEvaluationService {

    private final KieContainer kieContainer;

    public RuleFacts.PricingFact evaluatePricing(RuleFacts.PricingFact fact) {
        log.info("Evaluating pricing rules for order type: {}", fact.getOrderType());
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(fact);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
        return fact;
    }

    public RuleFacts.DispatchFact evaluateDispatch(RuleFacts.DispatchFact fact) {
        log.info("Evaluating dispatch rules for order type: {}", fact.getOrderType());
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(fact);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
        return fact;
    }

    public RuleFacts.SLAFact evaluateSla(RuleFacts.SLAFact fact) {
        log.info("Evaluating SLA rules for client tier: {} and order type: {}", fact.getClientTier(), fact.getOrderType());
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(fact);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
        return fact;
    }
}
