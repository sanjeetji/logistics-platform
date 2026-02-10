package com.logistics.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadRequest<T> {
    private List<T> data;
    private boolean skipDuplicates = true;
    private boolean clearExisting = false;
}
