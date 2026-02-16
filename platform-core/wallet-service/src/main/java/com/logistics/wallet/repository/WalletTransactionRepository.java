package com.logistics.wallet.repository;

import com.logistics.wallet.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String> {
    List<WalletTransaction> findByWalletId(String walletId);
    Page<WalletTransaction> findByWalletId(String walletId, Pageable pageable);
    Page<WalletTransaction> findByUserId(String userId, Pageable pageable);
}
