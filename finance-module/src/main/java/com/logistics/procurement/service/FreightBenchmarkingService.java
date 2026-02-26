package com.logistics.procurement.service;

import com.logistics.platform.common.client.ml.MlServiceClient;
import com.logistics.procurement.entity.RequestForQuote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreightBenchmarkingService {

    // private final MlServiceClient mlServiceClient; // Usually available in
    // shared-lib

    /**
     * Estimates a benchmark freight rate either from the ML Service or a local
     * heuristic fallback.
     */
    public BigDecimal benchmarkFreightRate(RequestForQuote rfq) {
        log.info("Benchmarking rate for RFQ {} from {} to {}", rfq.getRfqId(), rfq.getOriginCountry(),
                rfq.getDestinationCountry());

        try {
            // Attempt to hit ML service if available
            // return mlServiceClient.predictFreightRate(rfq.getOriginCountry(),
            // rfq.getDestinationCountry(), rfq.getTotalWeightKg());
            // Fallthrough to heuristic simulation for now
            return applyHeuristicBenchmark(rfq);
        } catch (Exception e) {
            log.warn(
                    "ML Freight Benchmark failed (ML Service down?). Falling back to standard heuristic model. Error: {}",
                    e.getMessage());
            return applyHeuristicBenchmark(rfq);
        }
    }

    private BigDecimal applyHeuristicBenchmark(RequestForQuote rfq) {
        BigDecimal baseRate = new BigDecimal("500.00");

        // International fallback multiplier
        if (!rfq.getOriginCountry().equalsIgnoreCase(rfq.getDestinationCountry())) {
            baseRate = baseRate.multiply(new BigDecimal("3.5"));
        }

        // Weight consideration ($2 per KG average)
        if (rfq.getTotalWeightKg() != null && rfq.getTotalWeightKg() > 0) {
            BigDecimal weightCost = new BigDecimal(rfq.getTotalWeightKg()).multiply(new BigDecimal("2.00"));
            baseRate = baseRate.add(weightCost);
        }

        log.debug("Heuristic benchmark calculated: ${}", baseRate);
        return baseRate;
    }
}
