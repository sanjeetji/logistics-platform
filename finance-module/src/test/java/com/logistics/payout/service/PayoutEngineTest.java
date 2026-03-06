package com.logistics.payout.service;

import com.logistics.payment.model.CODSettlement;
import com.logistics.payment.repository.CODSettlementRepository;
import com.logistics.payout.client.OrderServiceClient;
import com.logistics.payout.model.Payout;
import com.logistics.payout.repository.PayoutRepository;
import com.logistics.platform.common.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayoutEngineTest {

        @Mock
        private PayoutRepository payoutRepository;
        @Mock
        private CODSettlementRepository codSettlementRepository;
        @Mock
        private OrderServiceClient orderClient;

        @InjectMocks
        private PayoutGenerationService payoutGenerationService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        void testGenerateDailyPayouts_GigDriverWithCod_DeductsAmount() {
                // Arrange
                String driverId = "101";
                OrderServiceClient.OrderResponse order = OrderServiceClient.OrderResponse.builder()
                                .orderId("ORD-1")
                                .driverId(driverId)
                                .price(new BigDecimal("100.00"))
                                .build();

                when(orderClient.getCompletedOrders(any(), any()))
                                .thenReturn(ApiResponse.success(Collections.singletonList(order)));

                // Mock COD: Driver has 20.00 cash collected
                CODSettlement cod = CODSettlement.builder()
                                .amount(new BigDecimal("20.00"))
                                .status(CODSettlement.SettlementStatus.COLLECTED)
                                .build();
                when(codSettlementRepository.findByDriverIdAndStatus(driverId,
                                CODSettlement.SettlementStatus.COLLECTED))
                                .thenReturn(Collections.singletonList(cod));

                when(payoutRepository.save(any(Payout.class))).thenAnswer(i -> i.getArguments()[0]);

                // Act
                payoutGenerationService.generateDailyPayouts();

                // Assert
                // Expected: (100 * 0.80) - 20 = 60.00
                verify(payoutRepository).save(argThat(p -> p.getDriverId().equals(101L) &&
                                p.getAmount().compareTo(new BigDecimal("60.00")) == 0));
        }

        @Test
        void testGenerateDailyPayouts_CarrierPartner_AggregatesOrders() {
                // Arrange
                String partnerId = "PARTNER-A";
                OrderServiceClient.OrderResponse order1 = OrderServiceClient.OrderResponse.builder()
                                .orderId("ORD-1")
                                .partnerId(partnerId)
                                .price(new BigDecimal("100.00"))
                                .build();
                OrderServiceClient.OrderResponse order2 = OrderServiceClient.OrderResponse.builder()
                                .orderId("ORD-2")
                                .partnerId(partnerId)
                                .price(new BigDecimal("50.00"))
                                .build();

                when(orderClient.getCompletedOrders(any(), any()))
                                .thenReturn(ApiResponse.success(Arrays.asList(order1, order2)));
                when(payoutRepository.save(any(Payout.class))).thenAnswer(i -> i.getArguments()[0]);

                // Act
                payoutGenerationService.generateDailyPayouts();

                // Assert
                // Expected: (100 + 50) * 0.80 = 120.00
                verify(payoutRepository).save(argThat(p -> partnerId.equals(p.getPartnerId()) &&
                                p.getAmount().compareTo(new BigDecimal("120.00")) == 0));
        }
}
