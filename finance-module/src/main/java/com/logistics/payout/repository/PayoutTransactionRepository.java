package com.logistics.payout.repository;

import com.logistics.payout.model.PayoutTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayoutTransactionRepository extends JpaRepository<PayoutTransaction, Long> {
    List<PayoutTransaction> findByWalletIdOrderByCreatedAtDesc(String walletId);
}
