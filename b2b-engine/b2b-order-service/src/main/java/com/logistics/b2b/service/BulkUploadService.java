package com.logistics.b2b.service;

import com.logistics.b2b.dto.BulkUploadResult;
import com.logistics.b2b.dto.CreateB2BOrderRequest;
import com.logistics.b2b.dto.OrderStopDTO;
import com.logistics.b2b.model.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for bulk order upload from CSV/Excel
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkUploadService {

    private final B2BOrderService orderService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Process CSV file upload
     */
    public BulkUploadResult processCsvUpload(MultipartFile file, Long clientId) throws IOException, CsvException {
        log.info("Processing CSV upload for client: {}", clientId);

        BulkUploadResult result = BulkUploadResult.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .successfulOrderIds(new ArrayList<>())
                .errors(new ArrayList<>())
                .build();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            
            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                result.setTotalRecords(result.getTotalRecords() + 1);

                try {
                    CreateB2BOrderRequest request = parseCsvRow(row, clientId);
                    B2BOrder order = orderService.createOrder(request);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    result.getSuccessfulOrderIds().add(order.getOrderId());
                } catch (Exception e) {
                    result.setFailureCount(result.getFailureCount() + 1);
                    result.getErrors().add(BulkUploadResult.BulkUploadError.builder()
                            .rowNumber(i + 1)
                            .errorMessage(e.getMessage())
                            .rowData(String.join(",", row))
                            .build());
                }
            }
        }

        log.info("CSV upload completed. Success: {}, Failures: {}", result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    /**
     * Process Excel file upload
     */
    public BulkUploadResult processExcelUpload(MultipartFile file, Long clientId) throws IOException {
        log.info("Processing Excel upload for client: {}", clientId);

        BulkUploadResult result = BulkUploadResult.builder()
                .totalRecords(0)
                .successCount(0)
                .failureCount(0)
                .successfulOrderIds(new ArrayList<>())
                .errors(new ArrayList<>())
                .build();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                result.setTotalRecords(result.getTotalRecords() + 1);

                try {
                    CreateB2BOrderRequest request = parseExcelRow(row, clientId);
                    B2BOrder order = orderService.createOrder(request);
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    result.getSuccessfulOrderIds().add(order.getOrderId());
                } catch (Exception e) {
                    result.setFailureCount(result.getFailureCount() + 1);
                    result.getErrors().add(BulkUploadResult.BulkUploadError.builder()
                            .rowNumber(i + 1)
                            .errorMessage(e.getMessage())
                            .rowData(getRowData(row))
                            .build());
                }
            }
        }

        log.info("Excel upload completed. Success: {}, Failures: {}", result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    /**
     * Parse CSV row to order request
     */
    private CreateB2BOrderRequest parseCsvRow(String[] row, Long clientId) {
        // CSV format: priority, slaDeadline, pickupAddress, pickupLat, pickupLon, deliveryAddress, deliveryLat, deliveryLon, notes
        
        List<OrderStopDTO> stops = new ArrayList<>();
        
        // Pickup stop
        stops.add(OrderStopDTO.builder()
                .stopSequence(1)
                .stopType("PICKUP")
                .address(row[2])
                .latitude(Double.parseDouble(row[3]))
                .longitude(Double.parseDouble(row[4]))
                .build());
        
        // Delivery stop
        stops.add(OrderStopDTO.builder()
                .stopSequence(2)
                .stopType("DELIVERY")
                .address(row[5])
                .latitude(Double.parseDouble(row[6]))
                .longitude(Double.parseDouble(row[7]))
                .build());

        return CreateB2BOrderRequest.builder()
                .clientId(clientId)
                .priority(row[0])
                .slaDeadline(LocalDateTime.parse(row[1], DATE_FORMATTER))
                .stops(stops)
                .notes(row.length > 8 ? row[8] : null)
                .build();
    }

    /**
     * Parse Excel row to order request
     */
    private CreateB2BOrderRequest parseExcelRow(Row row, Long clientId) {
        List<OrderStopDTO> stops = new ArrayList<>();
        
        // Pickup stop
        stops.add(OrderStopDTO.builder()
                .stopSequence(1)
                .stopType("PICKUP")
                .address(getCellValue(row.getCell(2)))
                .latitude(Double.parseDouble(getCellValue(row.getCell(3))))
                .longitude(Double.parseDouble(getCellValue(row.getCell(4))))
                .build());
        
        // Delivery stop
        stops.add(OrderStopDTO.builder()
                .stopSequence(2)
                .stopType("DELIVERY")
                .address(getCellValue(row.getCell(5)))
                .latitude(Double.parseDouble(getCellValue(row.getCell(6))))
                .longitude(Double.parseDouble(getCellValue(row.getCell(7))))
                .build());

        return CreateB2BOrderRequest.builder()
                .clientId(clientId)
                .priority(getCellValue(row.getCell(0)))
                .slaDeadline(LocalDateTime.parse(getCellValue(row.getCell(1)), DATE_FORMATTER))
                .stops(stops)
                .notes(row.getCell(8) != null ? getCellValue(row.getCell(8)) : null)
                .build();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private String getRowData(Row row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            sb.append(getCellValue(row.getCell(i))).append(",");
        }
        return sb.toString();
    }
}
