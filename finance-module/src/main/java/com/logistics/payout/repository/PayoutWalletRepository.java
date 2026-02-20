package com.logistics.payout.repository;

import com.logistics.payout.model.PayoutWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayoutWalletRepository extends JpaRepository<PayoutWallet, Long> {
    Optional<PayoutWallet> findByDriverId(String driverId);
}
