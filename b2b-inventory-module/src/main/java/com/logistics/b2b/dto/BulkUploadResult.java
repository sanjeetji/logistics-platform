package com.logistics.b2b.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResult {
    
    private Integer totalRecords;
    private Integer successCount;
    private Integer failureCount;
    private List<String> successfulOrderIds;
    private List<BulkUploadError> errors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkUploadError {
        private Integer rowNumber;
        private String errorMessage;
        private String rowData;
    }
}
