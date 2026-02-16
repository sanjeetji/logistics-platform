package com.logistics.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.wallet.model.Wallet;
import com.logistics.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createWallet_ShouldReturnWallet() throws Exception {
        Wallet wallet = Wallet.builder().userId("user1").balance(BigDecimal.ZERO).build();
        when(walletService.createWallet("user1")).thenReturn(wallet);

        mockMvc.perform(post("/api/v1/wallets")
                .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void getWallet_ShouldReturnWallet() throws Exception {
        Wallet wallet = Wallet.builder().userId("user1").balance(BigDecimal.TEN).build();
        when(walletService.getWallet("user1")).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallets/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.balance").value(10));
    }

    @Test
    void topUp_ShouldReturnUpdatedWallet() throws Exception {
        Wallet wallet = Wallet.builder().userId("user1").balance(new BigDecimal("100")).build();
        when(walletService.topUp(eq("user1"), eq(new BigDecimal("100")), eq("ref1"))).thenReturn(wallet);

        Map<String, Object> request = new HashMap<>();
        request.put("amount", 100);
        request.put("referenceId", "ref1");

        mockMvc.perform(post("/api/v1/wallets/user1/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    void deduct_ShouldReturnUpdatedWallet() throws Exception {
        Wallet wallet = Wallet.builder().userId("user1").balance(new BigDecimal("50")).build();
        when(walletService.deduct(eq("user1"), eq(new BigDecimal("50")), eq("ref1"), eq("desc"))).thenReturn(wallet);

        Map<String, Object> request = new HashMap<>();
        request.put("amount", 50);
        request.put("referenceId", "ref1");
        request.put("description", "desc");

        mockMvc.perform(post("/api/v1/wallets/user1/deduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50));
    }
}
