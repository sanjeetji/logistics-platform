package com.logistics.customer.service;

import com.logistics.customer.model.Payment;
import com.logistics.customer.model.PaymentMethod;
import com.logistics.customer.model.PaymentStatus;
import com.logistics.customer.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for payment processing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Create payment for order
     */
    @Transactional
    public Payment createPayment(String orderId, Long customerId, BigDecimal amount, PaymentMethod method) {
        log.info("Creating payment for order: {} with method: {}", orderId, method);

        Payment payment = Payment.builder()
                .orderId(orderId)
                .customerId(customerId)
                .amount(amount)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * Process payment (mock implementation)
     */
    @Transactional
    public Payment processPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        // Mock payment processing
        if (payment.getPaymentMethod() == PaymentMethod.CASH) {
            // Cash payments are marked as success immediately
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
        } else {
            // Other methods would integrate with payment gateway
            payment.setPaymentStatus(PaymentStatus.PROCESSING);
        }

        return paymentRepository.save(payment);
    }

    /**
     * Get payment by order ID
     */
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    /**
     * Get customer payments
     */
    public List<Payment> getCustomerPayments(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
}
