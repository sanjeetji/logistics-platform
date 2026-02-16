package com.logistics.edi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EDI 856 - Advanced Ship Notice (ASN)
 * Notification of shipment details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edi856ShipmentNotice {
    
    // Header
    private String asnNumber;
    private String purchaseOrderNumber;
    private LocalDateTime shipDateTime;
    private String carrierId;
    private String carrierName;
    private String trackingNumber;
    
    // Ship From
    private String shipFromName;
    private String shipFromAddress;
    private String shipFromCity;
    private String shipFromState;
    private String shipFromZip;
    
    // Ship To
    private String shipToName;
    private String shipToAddress;
    private String shipToCity;
    private String shipToState;
    private String shipToZip;
    
    // Shipment Details
    private List<ShipmentItem> items;
    private Integer totalPackages;
    private BigDecimal totalWeight;
    private String weightUnit; // LB, KG
    private LocalDateTime estimatedDeliveryDate;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipmentItem {
        private String lineNumber;
        private String productCode;
        private String productDescription;
        private Integer quantityShipped;
        private String unitOfMeasure;
        private String serialNumber;
        private String lotNumber;
    }
    
    /**
     * Generate EDI X12 856 format
     */
    public String toEdiX12Format() {
        StringBuilder edi = new StringBuilder();
        
        // ST - Transaction Set Header
        edi.append("ST*856*0001~\n");
        
        // BSN - Beginning Segment for Ship Notice
        edi.append(String.format("BSN*00*%s*%s*%s~\n",
                asnNumber, shipDateTime, purchaseOrderNumber));
        
        // DTM - Ship Date/Time
        edi.append(String.format("DTM*011*%s~\n", shipDateTime));
        
        // N1 - Ship From
        edi.append(String.format("N1*SF*%s~\n", shipFromName));
        edi.append(String.format("N3*%s~\n", shipFromAddress));
        edi.append(String.format("N4*%s*%s*%s~\n",
                shipFromCity, shipFromState, shipFromZip));
        
        // N1 - Ship To
        edi.append(String.format("N1*ST*%s~\n", shipToName));
        edi.append(String.format("N3*%s~\n", shipToAddress));
        edi.append(String.format("N4*%s*%s*%s~\n",
                shipToCity, shipToState, shipToZip));
        
        // HL - Shipment Hierarchical Level
        edi.append("HL*1**S~\n");
        edi.append(String.format("TD5****%s*%s~\n", carrierName, trackingNumber));
        
        // Items
        int lineCount = 0;
        for (ShipmentItem item : items) {
            lineCount++;
            edi.append(String.format("HL*%d*1*I~\n", lineCount + 1));
            edi.append(String.format("LIN**BP*%s~\n", item.productCode));
            edi.append(String.format("SN1**%d*%s~\n",
                    item.quantityShipped, item.unitOfMeasure));
            
            if (item.serialNumber != null) {
                edi.append(String.format("REF*SN*%s~\n", item.serialNumber));
            }
        }
        
        // SE - Transaction Set Trailer
        edi.append(String.format("SE*%d*0001~\n", lineCount + 15));
        
        return edi.toString();
    }
}
