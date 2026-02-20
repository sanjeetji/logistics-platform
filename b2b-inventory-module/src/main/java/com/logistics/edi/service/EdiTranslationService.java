package com.logistics.edi.service;

import com.logistics.edi.model.Edi850PurchaseOrder;
import com.logistics.edi.model.Edi856ShipmentNotice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EdiTranslationService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public EdiTranslationService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    /**
     * Parse incoming EDI 850 Purchase Order
     */
    public Edi850PurchaseOrder parseEdi850(String ediContent) {
        log.info("Parsing EDI 850 document");
        
        Edi850PurchaseOrder po = new Edi850PurchaseOrder();
        
        // Parse BEG segment
        Pattern begPattern = Pattern.compile("BEG\\*\\d+\\*\\w+\\*([^*]+)\\*\\*([^~]+)~");
        Matcher begMatcher = begPattern.matcher(ediContent);
        if (begMatcher.find()) {
            po.setPurchaseOrderNumber(begMatcher.group(1));
        }
        
        // In production, implement full X12 parser
        log.info("Parsed purchase order: {}", po.getPurchaseOrderNumber());
        
        // Publish to Kafka
        kafkaTemplate.send("edi-850-received", po.getPurchaseOrderNumber(), po);
        
        return po;
    }
    
    /**
     * Generate EDI 850 from internal order
     */
    public String generateEdi850(Edi850PurchaseOrder purchaseOrder) {
        log.info("Generating EDI 850 for PO: {}", purchaseOrder.getPurchaseOrderNumber());
        
        String ediContent = purchaseOrder.toEdiX12Format();
        
        // Publish event
        kafkaTemplate.send("edi-850-sent", purchaseOrder.getPurchaseOrderNumber(), ediContent);
        
        return ediContent;
    }
    
    /**
     * Parse incoming EDI 856 Ship Notice
     */
    public Edi856ShipmentNotice parseEdi856(String ediContent) {
        log.info("Parsing EDI 856 document");
        
        Edi856ShipmentNotice asn = new Edi856ShipmentNotice();
        
        // Parse BSN segment
        Pattern bsnPattern = Pattern.compile("BSN\\*\\d+\\*([^*]+)\\*([^*]+)\\*([^~]+)~");
        Matcher bsnMatcher = bsnPattern.matcher(ediContent);
        if (bsnMatcher.find()) {
            asn.setAsnNumber(bsnMatcher.group(1));
            asn.setPurchaseOrderNumber(bsnMatcher.group(3));
        }
        
        log.info("Parsed ASN: {}", asn.getAsnNumber());
        
        // Publish to Kafka
        kafkaTemplate.send("edi-856-received", asn.getAsnNumber(), asn);
        
        return asn;
    }
    
    /**
     * Generate EDI 856 from shipment
     */
    public String generateEdi856(Edi856ShipmentNotice shipmentNotice) {
        log.info("Generating EDI 856 for ASN: {}", shipmentNotice.getAsnNumber());
        
        String ediContent = shipmentNotice.toEdiX12Format();
        
        // Publish event
        kafkaTemplate.send("edi-856-sent", shipmentNotice.getAsnNumber(), ediContent);
        
        return ediContent;
    }
    
    /**
     * Validate EDI structure
     */
    public boolean validateEdi(String ediContent) {
        if (ediContent == null || ediContent.isEmpty()) {
            return false;
        }
        
        // Check for basic EDI structure
        return ediContent.contains("ST*") && ediContent.contains("SE*");
    }
}
