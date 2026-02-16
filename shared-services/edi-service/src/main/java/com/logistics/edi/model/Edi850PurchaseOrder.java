package com.logistics.edi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * EDI 850 - Purchase Order
 * Standard B2B purchase order document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edi850PurchaseOrder {
    
    // Header
    private String purchaseOrderNumber;
    private LocalDate orderDate;
    private String buyerId; // Customer/buyer identifier
    private String sellerId; // Vendor/seller identifier
    
    // Shipping Info
    private String shipToName;
    private String shipToAddress;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    private String shipToCountry;
    
    // Billing Info
    private String billToName;
    private String billToAddress;
    private String billToCity;
    private String billToState;
    private String billToZip;
    private String billToCountry;
    
    // Order Details
    private List<LineItem> lineItems;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentTerms;
    private LocalDate requestedDeliveryDate;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        private String lineNumber;
        private String productCode;
        private String productDescription;
        private Integer quantity;
        private String unitOfMeasure; // EA (each), CS (case), etc.
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }
    
    /**
     * Generate EDI X12 850 format
     */
    public String toEdiX12Format() {
        StringBuilder edi = new StringBuilder();
        
        // ST - Transaction Set Header
        edi.append("ST*850*0001~\n");
        
        // BEG - Beginning Segment for Purchase Order
        edi.append(String.format("BEG*00*SA*%s**%s~\n", 
                purchaseOrderNumber, orderDate));
        
        // N1 - Buyer Name
        edi.append(String.format("N1*BY*%s*92*%s~\n", buyerId, buyerId));
        
        // N1 - Seller Name
        edi.append(String.format("N1*SE*%s*92*%s~\n", sellerId, sellerId));
        
        // N1 - Ship To  
        edi.append(String.format("N1*ST*%s~\n", shipToName));
        edi.append(String.format("N3*%s~\n", shipToAddress));
        edi.append(String.format("N4*%s*%s*%s*%s~\n", 
                shipToCity, shipToState, shipToZip, shipToCountry));
        
        // Line Items
        int lineCount = 0;
        for (LineItem item : lineItems) {
            lineCount++;
            // PO1 - Baseline Item Data
            edi.append(String.format("PO1*%s*%d*%s*%s**BP*%s*%s~\n",
                    item.lineNumber, item.quantity, item.unitOfMeasure,
                    item.unitPrice, item.productCode, item.productDescription));
        }
        
        // CTT - Transaction Totals
        edi.append(String.format("CTT*%d~\n", lineCount));
        
        // SE - Transaction Set Trailer
        edi.append(String.format("SE*%d*0001~\n", lineCount + 10));
        
        return edi.toString();
    }
}
