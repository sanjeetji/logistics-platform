package com.logistics.compliance.repository;

import com.logistics.compliance.model.ProofOfDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProofOfDeliveryRepository extends JpaRepository<ProofOfDelivery, Long> {

    Optional<ProofOfDelivery> findByPodId(String podId);

    Optional<ProofOfDelivery> findByOrderId(String orderId);

    List<ProofOfDelivery> findByVerified(Boolean verified);
}
