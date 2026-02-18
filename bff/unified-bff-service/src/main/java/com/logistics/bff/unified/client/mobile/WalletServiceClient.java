package com.logistics.bff.unified.client.mobile;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "wallet-service")
public interface WalletServiceClient {
    
    @GetMapping("/api/wallets/{userId}")
    Object getWalletBalance(@PathVariable String userId);
    
    @GetMapping("/api/wallets/{userId}/transactions")
    List<Object> getTransactions(@PathVariable String userId, @RequestParam int page, @RequestParam int size);
    
    @PostMapping("/api/wallets/{userId}/add-money")
    Object addMoney(@PathVariable String userId, @RequestBody Object request);
    
    @PostMapping("/api/wallets/{userId}/withdraw")
    Object withdrawMoney(@PathVariable String userId, @RequestBody Object request);
}
