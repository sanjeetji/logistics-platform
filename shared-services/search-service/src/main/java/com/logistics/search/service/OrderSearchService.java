package com.logistics.search.service;

import com.logistics.search.model.OrderDocument;
import com.logistics.search.repository.OrderSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSearchService {
    
    private final OrderSearchRepository orderSearchRepository;
    
    /**
     * Index or update an order in Elasticsearch
     */
    public OrderDocument indexOrder(OrderDocument order) {
        log.info("Indexing order: {}", order.getOrderId());
        return orderSearchRepository.save(order);
    }
    
    /**
     * Get order by ID
     */
    public Optional<OrderDocument> getOrderById(String orderId) {
        return orderSearchRepository.findById(orderId);
    }
    
    /**
     * Delete order from index
     */
    public void deleteOrder(String orderId) {
        log.info("Deleting order from index: {}", orderId);
        orderSearchRepository.deleteById(orderId);
    }
    
    /**
     * Full-text search across all order fields
     */
    public Page<OrderDocument> searchOrders(String query, int page, int size) {
        log.info("Searching orders with query: {}", query);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Search in customer names, driver names, and addresses
        return orderSearchRepository.searchByName(query, pageable);
    }
    
    /**
     * Search by customer ID
     */
    public Page<OrderDocument> searchByCustomer(String customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderSearchRepository.findByCustomerId(customerId, pageable);
    }
    
    /**
     * Search by driver ID
     */
    public Page<OrderDocument> searchByDriver(String driverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderSearchRepository.findByDriverId(driverId, pageable);
    }
    
    /**
     * Search by status
     */
    public Page<OrderDocument> searchByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderSearchRepository.findByStatus(status, pageable);
    }
    
    /**
     * Search by multiple statuses
     */
    public Page<OrderDocument> searchByStatuses(List<String> statuses, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderSearchRepository.findByStatusIn(statuses, pageable);
    }
    
    /**
     * Search by date range
     */
    public Page<OrderDocument> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                                  int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderSearchRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }
    
    /**
     * Search by address
     */
    public Page<OrderDocument> searchByAddress(String address, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderSearchRepository.searchByAddress(address, pageable);
    }
    
    /**
     * Advanced search with multiple criteria
     */
    public Page<OrderDocument> advancedSearch(String query, List<String> statuses, 
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               int page, int size) {
        log.info("Advanced search - query: {}, statuses: {}, date range: {} to {}", 
                 query, statuses, startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Default statuses if none provided
        if (statuses == null || statuses.isEmpty()) {
            statuses = Arrays.asList("CREATED", "ASSIGNED", "PICKED_UP", "IN_TRANSIT", "DELIVERED");
        }
        
        String startDateStr = startDate != null ? startDate.toString() : "2020-01-01T00:00:00";
        String endDateStr = endDate != null ? endDate.toString() : LocalDateTime.now().toString();
        
        return orderSearchRepository.advancedSearch(query, statuses, startDateStr, endDateStr, pageable);
    }
    
    /**
     * Autocomplete suggestions for customer/driver names
     */
    public Page<OrderDocument> autocomplete(String prefix, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderSearchRepository.searchByName(prefix, pageable);
    }
}
