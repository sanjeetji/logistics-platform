package com.logistics.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentStorageService {

    private final S3Client s3Client;

    @Value("${document.storage.bucket-name:logistics-documents}")
    private String bucketName;

    @Value("${document.storage.base-url:http://localhost:9000}")
    private String baseUrl;

    /**
     * Upload document to S3/MinIO
     */
    public String uploadDocument(MultipartFile file, String folder) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename(), folder);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String documentUrl = String.format("%s/%s/%s", baseUrl, bucketName, fileName);
        log.info("Uploaded document: {}", documentUrl);

        return documentUrl;
    }

    /**
     * Download document from S3/MinIO
     */
    public InputStream downloadDocument(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    /**
     * Delete document from S3/MinIO
     */
    public void deleteDocument(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("Deleted document: {}", fileName);
    }

    private String generateFileName(String originalFilename, String folder) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return String.format("%s/%s%s", folder, UUID.randomUUID(), extension);
    }
}
