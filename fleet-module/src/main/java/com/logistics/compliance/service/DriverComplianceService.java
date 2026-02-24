package com.logistics.compliance.service;

import com.logistics.compliance.model.Document;
import com.logistics.compliance.model.DocumentStatus;
import com.logistics.compliance.model.DocumentType;
import com.logistics.compliance.repository.DocumentRepository;
import com.logistics.fleet.model.Driver;
import com.logistics.fleet.model.VerificationStatus;
import com.logistics.fleet.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverComplianceService {

    private final DocumentRepository documentRepository;
    private final DriverRepository driverRepository;

    private static final Set<DocumentType> MANDATORY_DRIVER_DOCS = Set.of(
            DocumentType.DRIVER_LICENSE,
            DocumentType.BACKGROUND_CHECK,
            DocumentType.DRIVER_INSURANCE);

    @Transactional
    public Document uploadDriverDocument(Long driverId, DocumentType type, String fileName, String fileUrl,
            LocalDateTime expiryDate) {
        log.info("Uploading document {} for driver: {}", type, driverId);

        Document document = Document.builder()
                .documentId("DOC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .entityId(driverId.toString())
                .entityType("DRIVER")
                .documentType(type)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .status(DocumentStatus.PENDING)
                .uploadedAt(LocalDateTime.now())
                .expiryDate(expiryDate)
                .build();

        return documentRepository.save(document);
    }

    @Transactional
    public Document verifyDocument(String documentId, String verifiedBy) {
        Document document = documentRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setStatus(DocumentStatus.VERIFIED);
        document.setVerifiedBy(verifiedBy);
        document.setVerifiedAt(LocalDateTime.now());
        document = documentRepository.save(document);

        if ("DRIVER".equals(document.getEntityType())) {
            checkAndUpdateDriverCompliance(Long.parseLong(document.getEntityId()));
        }

        return document;
    }

    @Transactional
    public void checkAndUpdateDriverCompliance(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        List<Document> driverDocs = documentRepository.findByEntityIdAndEntityType(driverId.toString(), "DRIVER");

        Set<DocumentType> verifiedDocTypes = driverDocs.stream()
                .filter(doc -> doc.getStatus() == DocumentStatus.VERIFIED)
                .filter(doc -> doc.getExpiryDate() == null || doc.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(Document::getDocumentType)
                .collect(Collectors.toSet());

        boolean isCompliant = verifiedDocTypes.containsAll(MANDATORY_DRIVER_DOCS);

        if (isCompliant) {
            driver.setVerificationStatus(VerificationStatus.VERIFIED);
            log.info("Driver {} is now fully compliant and VERIFIED", driverId);
        } else {
            driver.setVerificationStatus(VerificationStatus.PENDING);
            log.info("Driver {} is NOT fully compliant yet", driverId);
        }

        driverRepository.save(driver);
    }
}
