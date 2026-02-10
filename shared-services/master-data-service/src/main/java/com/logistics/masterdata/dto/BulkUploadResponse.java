package com.logistics.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResponse {
    private int totalRecords;
    private int successCount;
    private int failureCount;
    private int skippedCount;
    private String message;
}
