package com.logistics.document.repository;

import com.logistics.document.model.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformDocumentRepository extends JpaRepository<DocumentMetadata, Long> {
}
