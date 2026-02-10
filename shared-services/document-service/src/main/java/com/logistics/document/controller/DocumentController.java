package com.logistics.document.controller;

import com.logistics.document.service.DocumentStorageService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        try {
            String documentUrl = storageService.uploadDocument(file, folder);
            return ResponseEntity.ok(ApiResponse.success(documentUrl, "Document uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to upload document: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(@PathVariable String fileName) {
        storageService.deleteDocument(fileName);
        return ResponseEntity.ok(ApiResponse.success("Deleted", "Document deleted successfully"));
    }
}
