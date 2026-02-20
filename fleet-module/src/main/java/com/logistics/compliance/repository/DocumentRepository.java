package com.logistics.compliance.repository;

import com.logistics.compliance.model.Document;
import com.logistics.compliance.model.DocumentStatus;
import com.logistics.compliance.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByDocumentId(String documentId);

    List<Document> findByOrderId(String orderId);

    List<Document> findByDocumentType(DocumentType documentType);

    List<Document> findByStatus(DocumentStatus status);

    List<Document> findByOrderIdAndDocumentType(String orderId, DocumentType documentType);
}
