package com.logistics.b2b.controller;

import com.logistics.b2b.dto.BulkUploadResult;
import com.logistics.b2b.dto.CreateB2BOrderRequest;
import com.logistics.b2b.model.B2BOrder;
import com.logistics.b2b.model.B2BOrderStatus;
import com.logistics.b2b.model.SLAStatus;
import com.logistics.b2b.service.B2BOrderService;
import com.logistics.b2b.service.BulkUploadService;
import com.logistics.b2b.service.SLAMonitoringService;
import com.logistics.platform.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/b2b/orders")
@RequiredArgsConstructor
public class B2BOrderController {

    private final B2BOrderService orderService;
    private final BulkUploadService bulkUploadService;
    private final SLAMonitoringService slaMonitoringService;

    @PostMapping
    public ResponseEntity<ApiResponse<B2BOrder>> createOrder(@Valid @RequestBody CreateB2BOrderRequest request) {
        B2BOrder order = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success(order, "Order created successfully"));
    }

    @PostMapping("/bulk/csv")
    public ResponseEntity<ApiResponse<BulkUploadResult>> bulkUploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientId") Long clientId) {
        try {
            BulkUploadResult result = bulkUploadService.processCsvUpload(file, clientId);
            return ResponseEntity.ok(ApiResponse.success(result, "CSV upload processed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process CSV: " + e.getMessage()));
        }
    }

    @PostMapping("/bulk/excel")
    public ResponseEntity<ApiResponse<BulkUploadResult>> bulkUploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientId") Long clientId) {
        try {
            BulkUploadResult result = bulkUploadService.processExcelUpload(file, clientId);
            return ResponseEntity.ok(ApiResponse.success(result, "Excel upload processed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process Excel: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<B2BOrder>> getOrder(@PathVariable String orderId) {
        B2BOrder order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<B2BOrder>>> getClientOrders(@PathVariable Long clientId) {
        List<B2BOrder> orders = orderService.getClientOrders(clientId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/sla/{status}")
    public ResponseEntity<ApiResponse<List<B2BOrder>>> getOrdersBySLA(@PathVariable SLAStatus status) {
        List<B2BOrder> orders = orderService.getOrdersBySLAStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<B2BOrder>> updateStatus(
            @PathVariable String orderId,
            @RequestParam B2BOrderStatus status) {
        B2BOrder order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Status updated"));
    }

    @PutMapping("/{orderId}/reschedule")
    public ResponseEntity<ApiResponse<B2BOrder>> rescheduleOrder(
            @PathVariable String orderId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDeadline) {
        B2BOrder order = orderService.rescheduleOrder(orderId, newDeadline);
        return ResponseEntity.ok(ApiResponse.success(order, "Order rescheduled"));
    }

    @GetMapping("/sla-report")
    public ResponseEntity<ApiResponse<SLAMonitoringService.SLAReport>> getSLAReport(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        SLAMonitoringService.SLAReport report = slaMonitoringService.getSLAReport(clientId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}
