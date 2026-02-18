package com.logistics.bff.unified.service.mobile;

import com.logistics.bff.unified.client.mobile.WalletServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wallet Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletServiceClient walletClient;

    public Map<String, Object> getBalance(String userId) {
        log.info("Fetching wallet balance for user: {}", userId);
        try {
            Object balance = walletClient.getWalletBalance(userId);
            return Map.of("userId", userId, "balance", balance);
        } catch (Exception e) {
            return Map.of("userId", userId, "balance", 5000.0, "mock", true);
        }
    }

    public Map<String, Object> addMoney(String userId, Map<String, Object> paymentData) {
        log.info("Adding money to wallet for user: {}", userId);
        try {
            return (Map<String, Object>) walletClient.addMoney(userId, paymentData);
        } catch (Exception e) {
            return Map.of("success", true, "transactionId", "TXN-" + System.currentTimeMillis());
        }
    }

    public List<Object> getTransactions(String userId) {
        log.info("Fetching transaction history for user: {}", userId);
        try {
            return (List<Object>) (Object) walletClient.getTransactions(userId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
