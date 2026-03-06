package com.logistics.compliance.controller;

import com.logistics.compliance.model.Document;
import com.logistics.compliance.model.DocumentType;
import com.logistics.compliance.service.DocumentService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compliance/documents")
@RequiredArgsConstructor
public class ComplianceDocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Document>> uploadDocument(
            @RequestParam String orderId,
            @RequestParam DocumentType documentType,
            @RequestParam("file") MultipartFile file,
            @RequestParam String uploadedBy) {
        try {
            Document document = documentService.uploadDocument(orderId, documentType, file, uploadedBy);
            return ResponseEntity.ok(ApiResponse.success(document, "Document uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to upload document: " + e.getMessage()));
        }
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Document>> getDocument(@PathVariable String documentId) {
        Document document = documentService.getDocumentById(documentId);
        return ResponseEntity.ok(ApiResponse.success(document));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<Document>>> getOrderDocuments(@PathVariable String orderId) {
        List<Document> documents = documentService.getOrderDocuments(orderId);
        return ResponseEntity.ok(ApiResponse.success(documents));
    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<Document>> verifyDocument(
            @PathVariable String documentId,
            @RequestParam String verifiedBy) {
        Document document = documentService.verifyDocument(documentId, verifiedBy);
        return ResponseEntity.ok(ApiResponse.success(document, "Document verified"));
    }

    @PutMapping("/{documentId}/reject")
    public ResponseEntity<ApiResponse<Document>> rejectDocument(
            @PathVariable String documentId,
            @RequestParam String reason,
            @RequestParam String verifiedBy) {
        Document document = documentService.rejectDocument(documentId, reason, verifiedBy);
        return ResponseEntity.ok(ApiResponse.success(document, "Document rejected"));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Document>>> getPendingDocuments() {
        List<Document> documents = documentService.getPendingDocuments();
        return ResponseEntity.ok(ApiResponse.success(documents));
    }
}
