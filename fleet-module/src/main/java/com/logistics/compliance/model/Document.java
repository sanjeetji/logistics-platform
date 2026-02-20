package com.logistics.compliance.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String documentId;

    @Column(nullable = false)
    private String orderId; // Reference to B2B order

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Column(nullable = false)
    private String fileName;

    private String filePath; // Server file path

    private String fileUrl; // Public URL

    private Long fileSize; // in bytes

    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.PENDING;

    private String uploadedBy;

    private LocalDateTime uploadedAt;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(columnDefinition = "text")
    private String rejectionReason;
}
