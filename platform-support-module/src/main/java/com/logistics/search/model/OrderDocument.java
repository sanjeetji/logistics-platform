package com.logistics.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "orders")
public class OrderDocument {
    
    @Id
    private String orderId;
    
    @Field(type = FieldType.Text)
    private String customerId;
    
    @Field(type = FieldType.Text)
    private String customerName;
    
    @Field(type = FieldType.Text)
    private String driverId;
    
    @Field(type = FieldType.Text)
    private String driverName;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Text)
    private String pickupAddress;
    
    @Field(type = FieldType.Text)
    private String deliveryAddress;
    
    @Field(type = FieldType.Double)
    private Double totalAmount;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
    
    @Field(type = FieldType.Keyword)
    private String priority;
    
    @Field(type = FieldType.Keyword)
    private String orderType; // B2B, B2C
    
    @Field(type = FieldType.Text)
    private String notes;
    
    @Field(type = FieldType.Keyword)
    private List<String> tags;
    
    @Field(type = FieldType.Object)
    private LocationInfo pickupLocation;
    
    @Field(type = FieldType.Object)
    private LocationInfo deliveryLocation;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private Double latitude;
        private Double longitude;
        private String address;
        private String city;
        private String state;
        private String zipCode;
    }
}
