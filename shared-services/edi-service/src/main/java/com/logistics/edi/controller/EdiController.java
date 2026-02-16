package com.logistics.edi.controller;

import com.logistics.edi.model.Edi850PurchaseOrder;
import com.logistics.edi.model.Edi856ShipmentNotice;
import com.logistics.edi.service.EdiTranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edi")
@RequiredArgsConstructor
@Tag(name = "EDI Integration", description = "B2B EDI document processing - 850, 856")
public class EdiController {
    
    private final EdiTranslationService ediService;
    
    // EDI 850 - Purchase Order
    @PostMapping("/850/parse")
    @Operation(summary = "Parse incoming EDI 850 Purchase Order")
    public ResponseEntity<Edi850PurchaseOrder> parseEdi850(@RequestBody String ediContent) {
        Edi850PurchaseOrder po = ediService.parseEdi850(ediContent);
        return ResponseEntity.ok(po);
    }
    
    @PostMapping("/850/generate")
    @Operation(summary = "Generate EDI 850 from Purchase Order")
    public ResponseEntity<String> generateEdi850(@RequestBody Edi850PurchaseOrder purchaseOrder) {
        String ediContent = ediService.generateEdi850(purchaseOrder);
        return ResponseEntity.ok(ediContent);
    }
    
    // EDI 856 - Shipment Notice
    @PostMapping("/856/parse")
    @Operation(summary = "Parse incoming EDI 856 Shipment Notice")
    public ResponseEntity<Edi856ShipmentNotice> parseEdi856(@RequestBody String ediContent) {
        Edi856ShipmentNotice asn = ediService.parseEdi856(ediContent);
        return ResponseEntity.ok(asn);
    }
    
    @PostMapping("/856/generate")
    @Operation(summary = "Generate EDI 856 from Shipment Notice")
    public ResponseEntity<String> generateEdi856(@RequestBody Edi856ShipmentNotice shipmentNotice) {
        String ediContent = ediService.generateEdi856(shipmentNotice);
        return ResponseEntity.ok(ediContent);
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate EDI document structure")
    public ResponseEntity<Boolean> validateEdi(@RequestBody String ediContent) {
        boolean valid = ediService.validateEdi(ediContent);
        return ResponseEntity.ok(valid);
    }
}
