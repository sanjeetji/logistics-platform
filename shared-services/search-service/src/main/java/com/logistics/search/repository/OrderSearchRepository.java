package com.logistics.search.repository;

import com.logistics.search.model.OrderDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderSearchRepository extends ElasticsearchRepository<OrderDocument, String> {
    
    // Find by status
    Page<OrderDocument> findByStatus(String status, Pageable pageable);
    
    // Find by customer ID
    Page<OrderDocument> findByCustomerId(String customerId, Pageable pageable);
    
    // Find by driver ID
    Page<OrderDocument> findByDriverId(String driverId, Pageable pageable);
    
    // Find by customer or driver name (full-text search)
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"customerName\", \"driverName\"]}}")
    Page<OrderDocument> searchByName(String name, Pageable pageable);
    
    // Find by address (full-text search)
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"pickupAddress\", \"deliveryAddress\"]}}")
    Page<OrderDocument> searchByAddress(String address, Pageable pageable);
    
    // Find by date range
    Page<OrderDocument> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find by multiple statuses
    Page<OrderDocument> findByStatusIn(List<String> statuses, Pageable pageable);
    
    // Find by order type
    Page<OrderDocument> findByOrderType(String orderType, Pageable pageable);
    
    // Find by priority
    Page<OrderDocument> findByPriority(String priority, Pageable pageable);
    
    // Complex search query for multiple criteria
    @Query("{\"bool\": {" +
           "\"must\": [" +
           "{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"customerName\", \"driverName\", \"pickupAddress\", \"deliveryAddress\", \"notes\"]}}" +
           "]," +
           "\"filter\": [" +
           "{\"terms\": {\"status\": ?1}}," +
           "{\"range\": {\"createdAt\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}" +
           "]}}")
    Page<OrderDocument> advancedSearch(String query, List<String> statuses, 
                                       String startDate, String endDate, Pageable pageable);
}
