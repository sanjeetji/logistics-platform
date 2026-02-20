package com.logistics.platform.client.rules;

import com.logistics.platform.common.dto.rules.RuleFacts;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rules-engine-service", path = "/api/rules/evaluate")
public interface RulesEngineClient {

    @PostMapping("/pricing")
    RuleFacts.PricingFact evaluatePricing(@RequestBody RuleFacts.PricingFact fact);

    @PostMapping("/dispatch")
    RuleFacts.DispatchFact evaluateDispatch(@RequestBody RuleFacts.DispatchFact fact);

    @PostMapping("/sla")
    RuleFacts.SlaFact evaluateSla(@RequestBody RuleFacts.SlaFact fact);
}
