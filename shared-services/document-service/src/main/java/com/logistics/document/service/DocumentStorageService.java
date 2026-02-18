package com.logistics.document.service;

import com.logistics.document.model.DocumentMetadata;
import com.logistics.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentStorageService {

    private final DocumentRepository documentRepository;
    private final io.awspring.cloud.s3.S3Template s3Template;

    @org.springframework.beans.factory.annotation.Value("${aws.s3.bucket-name:logistics-documents}")
    private String bucketName;

    public DocumentMetadata uploadFile(MultipartFile file) {
        String key = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            // Upload to S3 using S3Template
            s3Template.upload(bucketName, key, file.getInputStream());

            DocumentMetadata metadata = DocumentMetadata.builder()
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storagePath(key) // Storing Key relative to bucket
                    .uploadedAt(LocalDateTime.now())
                    .build();

            return documentRepository.save(metadata);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read file input stream", e);
        } catch (Exception e) {
            // Check if it's an S3 config issue (e.g. valid credentials missing)
            // Fallback provided? In this case, we rethrow for now or could save local.
            throw new RuntimeException("Failed to upload to S3: " + e.getMessage(), e);
        }
    }

    public DocumentMetadata getDocumentMetadata(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public String getPresignedUrl(Long id) {
        DocumentMetadata meta = getDocumentMetadata(id);
        try {
            // Generate presigned URL valid for 1 hour
            java.net.URL url = s3Template.createSignedGetURL(bucketName, meta.getStoragePath(),
                    java.time.Duration.ofHours(1));
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}
