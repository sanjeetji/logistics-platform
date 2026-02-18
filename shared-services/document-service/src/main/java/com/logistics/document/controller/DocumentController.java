package com.logistics.document.controller;

import com.logistics.document.model.DocumentMetadata;
import com.logistics.document.service.DocumentStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentMetadata> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(storageService.uploadFile(file));
    }

    @GetMapping("/{id}/metadata")
    public ResponseEntity<DocumentMetadata> getMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(storageService.getDocumentMetadata(id));
    }
}
