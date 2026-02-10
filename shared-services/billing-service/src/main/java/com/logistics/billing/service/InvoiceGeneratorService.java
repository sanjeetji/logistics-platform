package com.logistics.billing.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.logistics.billing.entity.Invoice;
import com.logistics.billing.entity.InvoiceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class InvoiceGeneratorService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Header
            document.add(new Paragraph("INVOICE").setFontSize(20).setBold());
            document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Client ID: " + invoice.getClientId()));
            document.add(new Paragraph("Date: " + invoice.getCreatedAt().toLocalDate()));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate()));
            document.add(new Paragraph("\n"));

            // Table
            Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 2, 2, 2 }));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell("Description");
            table.addHeaderCell("Quantity");
            table.addHeaderCell("Unit Price");
            table.addHeaderCell("Total");

            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    table.addCell(item.getDescription() != null ? item.getDescription() : "Item");
                    table.addCell(String.valueOf(item.getQuantity()));
                    table.addCell(item.getAmount().toString());
                    table.addCell(
                            item.getAmount().multiply(java.math.BigDecimal.valueOf(item.getQuantity())).toString());
                }
            }

            document.add(table);

            // Total
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total Amount: $" + invoice.getTotalAmount()).setBold().setFontSize(14));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF invoice", e);
            throw new RuntimeException("Failed to generate PDF invoice", e);
        }
    }
}
