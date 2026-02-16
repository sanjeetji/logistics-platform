package com.logistics.search.controller;

import com.logistics.search.model.OrderDocument;
import com.logistics.search.service.OrderSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/search/orders")
@RequiredArgsConstructor
@Tag(name = "Order Search", description = "Elasticsearch-based order search API")
public class SearchController {
    
    private final OrderSearchService orderSearchService;
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDocument> getOrder(@PathVariable String orderId) {
        return orderSearchService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Full-text search orders")
    public ResponseEntity<Page<OrderDocument>> searchOrders(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OrderDocument> results = orderSearchService.searchOrders(q, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Search orders by customer")
    public ResponseEntity<Page<OrderDocument>> searchByCustomer(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OrderDocument> results = orderSearchService.searchByCustomer(customerId, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Search orders by driver")
    public ResponseEntity<Page<OrderDocument>> searchByDriver(
            @PathVariable String driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OrderDocument> results = orderSearchService.searchByDriver(driverId, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Search orders by status")
    public ResponseEntity<Page<OrderDocument>> searchByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OrderDocument> results = orderSearchService.searchByStatus(status, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/address")
    @Operation(summary = "Search orders by address")
    public ResponseEntity<Page<OrderDocument>> searchByAddress(
            @RequestParam String address,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OrderDocument> results = orderSearchService.searchByAddress(address, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Search orders by date range")
    public ResponseEntity<Page<OrderDocument>> searchByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OrderDocument> results = orderSearchService.searchByDateRange(startDate, endDate, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/advanced")
    @Operation(summary = "Advanced search with multiple criteria")
    public ResponseEntity<Page<OrderDocument>> advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        String query = q != null ? q : "";
        Page<OrderDocument> results = orderSearchService.advancedSearch(
                query, statuses, startDate, endDate, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/autocomplete")
    @Operation(summary = "Auto complete suggestions")
    public ResponseEntity<Page<OrderDocument>> autocomplete(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<OrderDocument> results = orderSearchService.autocomplete(prefix, page, size);
        return ResponseEntity.ok(results);
    }
    
    @PostMapping
    @Operation(summary = "Index an order (internal use)")
    public ResponseEntity<OrderDocument> indexOrder(@RequestBody OrderDocument order) {
        OrderDocument indexed = orderSearchService.indexOrder(order);
        return ResponseEntity.ok(indexed);
    }
    
    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order from index (internal use)")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        orderSearchService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
