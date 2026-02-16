package com.logistics.payment.repository;

import com.logistics.payment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);

    List<Transaction> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
