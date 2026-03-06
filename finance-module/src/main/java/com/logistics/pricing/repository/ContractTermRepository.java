package com.logistics.pricing.repository;

import com.logistics.pricing.model.ContractTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTermRepository extends JpaRepository<ContractTerm, Long> {

    List<ContractTerm> findByContractId(Long contractId);

    Optional<ContractTerm> findByContractIdAndVehicleType(Long contractId, String vehicleType);
}
