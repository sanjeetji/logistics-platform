package com.logistics.customer.repository;

import com.logistics.customer.model.Payment;
import com.logistics.customer.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(String orderId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByCustomerId(Long customerId);
    
    List<Payment> findByCustomerIdAndPaymentStatus(Long customerId, PaymentStatus status);
}
