package com.logistics.payment.repository;

import com.logistics.payment.model.PaymentWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentWalletRepository extends JpaRepository<PaymentWallet, Long> {
    Optional<PaymentWallet> findByUserId(Long userId);
}
