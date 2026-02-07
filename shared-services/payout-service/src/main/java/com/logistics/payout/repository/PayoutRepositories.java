package com.logistics.payout.repository;

import com.logistics.payout.model.PayoutRequest;
import com.logistics.payout.model.Transaction;
import com.logistics.payout.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByDriverId(String driverId);
}

@Repository
interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(String walletId);
}

@Repository
interface PayoutRequestRepository extends JpaRepository<PayoutRequest, Long> {
    List<PayoutRequest> findByDriverId(String driverId);
    List<PayoutRequest> findByStatus(PayoutRequest.PayoutStatus status);
}
