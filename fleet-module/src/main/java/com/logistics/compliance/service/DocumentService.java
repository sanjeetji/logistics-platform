package com.logistics.compliance.service;

import com.logistics.compliance.model.Document;
import com.logistics.compliance.model.DocumentStatus;
import com.logistics.compliance.model.DocumentType;
import com.logistics.compliance.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service for document management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${document.storage.path:/var/logistics/documents}")
    private String storagePath;

    @Value("${document.storage.url:http://localhost:8090/api/v1/documents/files}")
    private String storageUrl;

    /**
     * Upload document
     */
    @Transactional
    public Document uploadDocument(String orderId, DocumentType documentType,
            MultipartFile file, String uploadedBy) throws IOException {
        log.info("Uploading {} document for order {}", documentType, orderId);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Create storage directory if not exists
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // Create document record
        Document document = Document.builder()
                .documentId("DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(orderId)
                .documentType(documentType)
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .fileUrl(storageUrl + "/" + filename)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .status(DocumentStatus.UPLOADED)
                .uploadedBy(uploadedBy)
                .uploadedAt(LocalDateTime.now())
                .build();

        return Objects
                .requireNonNull(documentRepository.save(Objects.requireNonNull(document, "Document must not be null")));
    }

    /**
     * Get document by ID
     */
    public Document getDocumentById(String documentId) {
        return documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
    }

    /**
     * Get order documents
     */
    public List<Document> getOrderDocuments(String orderId) {
        return documentRepository.findByOrderId(orderId);
    }

    /**
     * Verify document
     */
    @Transactional
    public Document verifyDocument(String documentId, String verifiedBy) {
        Document document = getDocumentById(documentId);
        document.setStatus(DocumentStatus.VERIFIED);
        document.setVerifiedBy(verifiedBy);
        document.setVerifiedAt(LocalDateTime.now());
        return Objects.requireNonNull(documentRepository.save(document));
    }

    /**
     * Reject document
     */
    @Transactional
    public Document rejectDocument(String documentId, String reason, String verifiedBy) {
        Document document = getDocumentById(documentId);
        document.setStatus(DocumentStatus.REJECTED);
        document.setRejectionReason(reason);
        document.setVerifiedBy(verifiedBy);
        document.setVerifiedAt(LocalDateTime.now());
        return Objects.requireNonNull(documentRepository.save(document));
    }

    /**
     * Get pending documents
     */
    public List<Document> getPendingDocuments() {
        return documentRepository.findByStatus(DocumentStatus.PENDING);
    }
}
