package com.logistics.pricing.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.pricing.model.ContractTerm;
import com.logistics.pricing.model.EnterpriseContract;
import com.logistics.pricing.repository.ContractTermRepository;
import com.logistics.pricing.repository.EnterpriseContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pricing/admin/contracts")
@RequiredArgsConstructor
public class ContractAdminController {

    private final EnterpriseContractRepository contractRepository;
    private final ContractTermRepository contractTermRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<EnterpriseContract>> createContract(@RequestBody EnterpriseContract contract) {
        EnterpriseContract saved = contractRepository.save(contract);
        return ResponseEntity.ok(ApiResponse.success(saved, "Contract created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnterpriseContract>>> getAllContracts() {
        return ResponseEntity.ok(ApiResponse.success(contractRepository.findAll()));
    }

    @PostMapping("/{contractId}/terms")
    public ResponseEntity<ApiResponse<ContractTerm>> addTermToContract(
            @PathVariable Long contractId,
            @RequestBody ContractTerm term) {

        EnterpriseContract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        term.setContract(contract);
        ContractTerm saved = contractTermRepository.save(term);

        return ResponseEntity.ok(ApiResponse.success(saved, "Term added successfully"));
    }

    @GetMapping("/{contractId}/terms")
    public ResponseEntity<ApiResponse<List<ContractTerm>>> getContractTerms(@PathVariable Long contractId) {
        return ResponseEntity.ok(ApiResponse.success(contractTermRepository.findByContractId(contractId)));
    }
}
