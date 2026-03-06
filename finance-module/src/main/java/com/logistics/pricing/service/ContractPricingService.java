package com.logistics.pricing.service;

import com.logistics.pricing.model.ContractTerm;
import com.logistics.pricing.model.EnterpriseContract;
import com.logistics.pricing.repository.ContractTermRepository;
import com.logistics.pricing.repository.EnterpriseContractRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractPricingService {

    private final EnterpriseContractRepository contractRepository;
    private final ContractTermRepository contractTermRepository;

    public Optional<ContractPriceResult> calculateContractPrice(String clientId, String vehicleType, double distanceKm,
            int estimatedMinutes) {
        if (clientId == null || clientId.isBlank()) {
            return Optional.empty();
        }

        // 1. Find active contract for client today
        Optional<EnterpriseContract> activeContractOpt = contractRepository
                .findActiveContractByClientIdAndDate(clientId, LocalDateTime.now());

        if (activeContractOpt.isEmpty()) {
            log.debug("No active enterprise contract found for client: {}", clientId);
            return Optional.empty();
        }

        EnterpriseContract contract = activeContractOpt.get();
        log.info("Found active contract: {} for client: {}", contract.getContractName(), clientId);

        // 2. Find specific vehicle terms
        Optional<ContractTerm> termOpt = contractTermRepository.findByContractIdAndVehicleType(contract.getId(),
                vehicleType);

        if (termOpt.isPresent()) {
            ContractTerm term = termOpt.get();
            BigDecimal calculatedPrice = calculateFromTerm(term, distanceKm, estimatedMinutes);
            return Optional.of(new ContractPriceResult(calculatedPrice, contract.getContractName(), true));
        }

        // 3. Fallback: Check if there is a generic discount term
        Optional<ContractTerm> genericTermOpt = contractTermRepository.findByContractIdAndVehicleType(contract.getId(),
                "ALL");
        if (genericTermOpt.isPresent()) {
            ContractTerm genericTerm = genericTermOpt.get();
            if (genericTerm.getDiscountPercentage() != null
                    && genericTerm.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                // The caller (DynamicPricingController) will need to apply this discount to the
                // standard rate
                return Optional.of(new ContractPriceResult(null, contract.getContractName(), false,
                        genericTerm.getDiscountPercentage()));
            }
        }

        log.debug("Contract exists but no applicable terms or discounts for vehicle type: {}", vehicleType);
        return Optional.empty();
    }

    private BigDecimal calculateFromTerm(ContractTerm term, double distanceKm, int estimatedMinutes) {
        BigDecimal base = term.getBaseRate() != null ? term.getBaseRate() : BigDecimal.ZERO;

        BigDecimal distanceCost = BigDecimal.ZERO;
        if (term.getPerKmRate() != null) {
            distanceCost = term.getPerKmRate().multiply(BigDecimal.valueOf(distanceKm));
        }

        BigDecimal timeCost = BigDecimal.ZERO;
        if (term.getPerMinuteRate() != null) {
            timeCost = term.getPerMinuteRate().multiply(BigDecimal.valueOf(estimatedMinutes));
        }

        return base.add(distanceCost).add(timeCost).setScale(2, RoundingMode.HALF_UP);
    }

    @Data
    @Builder
    public static class ContractPriceResult {
        private BigDecimal finalPrice; // Custom calculated price
        private String contractName;
        private boolean isOverride; // true if specific rate used, false if we just provide a discount
        private BigDecimal discountPercentage; // E.g., 15.00 for 15% off standard rates

        public ContractPriceResult(BigDecimal finalPrice, String contractName, boolean isOverride) {
            this.finalPrice = finalPrice;
            this.contractName = contractName;
            this.isOverride = isOverride;
            this.discountPercentage = BigDecimal.ZERO;
        }

        public ContractPriceResult(BigDecimal finalPrice, String contractName, boolean isOverride,
                BigDecimal discountPercentage) {
            this.finalPrice = finalPrice;
            this.contractName = contractName;
            this.isOverride = isOverride;
            this.discountPercentage = discountPercentage;
        }
    }
}
