package com.logistics.bff.unified.client.mobile;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "wallet-service")
public interface WalletServiceClient {
    @GetMapping("/api/wallets/{userId}")
    Object getWalletBalance(@PathVariable("userId") String userId);

    @GetMapping("/api/wallets/{userId}/transactions")
    List<Object> getTransactions(@PathVariable("userId") String userId);

    @PostMapping("/api/wallets/{userId}/add-money")
    Object addMoney(@PathVariable("userId") String userId, @RequestBody Object request);

    @PostMapping("/api/wallets/{userId}/withdraw")
    Object withdrawMoney(@PathVariable("userId") String userId, @RequestBody Object request);
}
