package com.logistics.billing.repository;

import com.logistics.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    List<Invoice> findByClientId(String clientId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
