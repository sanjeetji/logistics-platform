package com.logistics.wallet.service;

import com.logistics.platform.event.dto.WalletCreditedEvent;
import com.logistics.platform.event.dto.WalletDebitedEvent;
import com.logistics.wallet.model.TransactionType;
import com.logistics.wallet.model.Wallet;
import com.logistics.wallet.model.WalletTransaction;
import com.logistics.wallet.repository.WalletRepository;
import com.logistics.wallet.repository.WalletTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletTransactionRepository transactionRepository;
    @Mock
    private KafkaOperations<String, Object> kafkaTemplate;

    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletServiceImpl(walletRepository, transactionRepository, kafkaTemplate);
    }

    @Test
    void createWallet_ShouldCreateNewWallet_WhenNotExists() {
        when(walletRepository.findByUserId("user1")).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet wallet = walletService.createWallet("user1");

        assertNotNull(wallet);
        assertEquals("user1", wallet.getUserId());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_ShouldReturnExisting_WhenExists() {
        Wallet existing = Wallet.builder().userId("user1").balance(BigDecimal.TEN).build();
        when(walletRepository.findByUserId("user1")).thenReturn(Optional.of(existing));

        Wallet wallet = walletService.createWallet("user1");

        assertEquals(existing, wallet);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void topUp_ShouldIncreaseBalanceAndPublishEvent() {
        Wallet wallet = Wallet.builder().walletId("w1").userId("user1").balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId("user1")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(any(WalletTransaction.class))).thenAnswer(i -> i.getArguments()[0]);

        BigDecimal amount = new BigDecimal("100.00");
        Wallet updatedWallet = walletService.topUp("user1", amount, "ref1");

        assertEquals(amount, updatedWallet.getBalance());
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(any(WalletTransaction.class));
        verify(kafkaTemplate).send(eq("wallet-events"), eq("user1"), any(WalletCreditedEvent.class));
    }

    @Test
    void deduct_ShouldDecreaseBalanceAndPublishEvent() {
        Wallet wallet = Wallet.builder().walletId("w1").userId("user1").balance(new BigDecimal("100.00")).build();
        when(walletRepository.findByUserId("user1")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(any(WalletTransaction.class))).thenAnswer(i -> i.getArguments()[0]);

        BigDecimal amount = new BigDecimal("50.00");
        Wallet updatedWallet = walletService.deduct("user1", amount, "ref1", "Test Deduct");

        assertEquals(new BigDecimal("50.00"), updatedWallet.getBalance());
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(any(WalletTransaction.class));
        verify(kafkaTemplate).send(eq("wallet-events"), eq("user1"), any(WalletDebitedEvent.class));
    }

    @Test
    void deduct_ShouldThrowException_WhenInsufficientBalance() {
        Wallet wallet = Wallet.builder().walletId("w1").userId("user1").balance(BigDecimal.ZERO).build();
        when(walletRepository.findByUserId("user1")).thenReturn(Optional.of(wallet));

        assertThrows(RuntimeException.class, () -> 
            walletService.deduct("user1", new BigDecimal("10.00"), "ref1", "Fail")
        );
        
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}
