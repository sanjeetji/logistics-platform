package com.logistics.masterdata.controller;

import com.logistics.masterdata.dto.BulkUploadRequest;
import com.logistics.masterdata.dto.BulkUploadResponse;
import com.logistics.masterdata.model.City;
import com.logistics.masterdata.model.VehicleType;
import com.logistics.masterdata.service.MasterDataService;
import com.logistics.platform.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/master-data/bulk")
@RequiredArgsConstructor
public class BulkUploadController {

    private final MasterDataService masterDataService;

    @PostMapping("/cities")
    public ResponseEntity<ApiResponse<BulkUploadResponse>> bulkUploadCities(
            @RequestBody BulkUploadRequest<City> request) {
        BulkUploadResponse response = masterDataService.bulkUploadCities(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Bulk upload completed"));
    }

    @PostMapping("/vehicle-types")
    public ResponseEntity<ApiResponse<BulkUploadResponse>> bulkUploadVehicleTypes(
            @RequestBody BulkUploadRequest<VehicleType> request) {
        BulkUploadResponse response = masterDataService.bulkUploadVehicleTypes(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Bulk upload completed"));
    }

    @PostMapping("/cache/clear")
    public ResponseEntity<ApiResponse<String>> clearCaches() {
        masterDataService.clearAllCaches();
        return ResponseEntity.ok(ApiResponse.success("Caches cleared", "All master data caches have been cleared"));
    }
}
