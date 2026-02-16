package com.logistics.wallet.service;

import com.logistics.wallet.model.Wallet;
import java.math.BigDecimal;

public interface WalletService {
    Wallet createWallet(String userId);
    Wallet getWallet(String userId);
    Wallet topUp(String userId, BigDecimal amount, String referenceId);
    Wallet deduct(String userId, BigDecimal amount, String referenceId, String description);
}
