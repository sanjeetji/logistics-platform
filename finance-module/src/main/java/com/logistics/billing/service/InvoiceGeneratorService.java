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
import lombok.RequiredArgsConstructor;
import com.logistics.tax.service.TaxCalculationService;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceGeneratorService {

    private final TaxCalculationService taxCalculationService;

    public byte[] generateInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Header Styling
            Paragraph header = new Paragraph("LOGISTIC PLATFORM INVOICE")
                    .setFontSize(22)
                    .setBold()
                    .setMarginBottom(10);
            document.add(header);

            document.add(new Paragraph("Invoice Number: " + invoice.getInvoiceNumber()).setBold());
            document.add(new Paragraph("Client ID: " + invoice.getClientId()));
            document.add(new Paragraph("Issue Date: " + invoice.getCreatedAt().toLocalDate()));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate()).setMarginBottom(20));

            // Table Structure
            Table table = new Table(UnitValue.createPercentArray(new float[] { 5, 1, 2, 2 }));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Paragraph("Description").setBold());
            table.addHeaderCell(new Paragraph("Qty").setBold());
            table.addHeaderCell(new Paragraph("Unit Price").setBold());
            table.addHeaderCell(new Paragraph("Line Total").setBold());

            java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;

            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    table.addCell(item.getDescription() != null ? item.getDescription() : "Service rendered");
                    table.addCell(String.valueOf(item.getQuantity()));
                    table.addCell("$" + item.getAmount().toString());

                    java.math.BigDecimal lineTotal = item.getAmount()
                            .multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                    subtotal = subtotal.add(lineTotal);
                    table.addCell("$" + lineTotal.toString());
                }
            }

            document.add(table);

            // Fetch client's country code (mocking this for now as US)
            String countryCode = "US";

            // Dynamic Calculations
            TaxCalculationService.TaxResult taxResult = taxCalculationService.calculateTax(subtotal, countryCode);

            // Totals Section
            document.add(new Paragraph("\n"));
            Table totalsTable = new Table(UnitValue.createPercentArray(new float[] { 8, 2 }));
            totalsTable.setWidth(UnitValue.createPercentValue(100));

            totalsTable.addCell(
                    new Paragraph("Subtotal:").setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            totalsTable.addCell("$" + subtotal.toString());

            totalsTable.addCell(
                    new Paragraph(taxResult.getTaxName() + ":")
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            totalsTable.addCell("$" + taxResult.getTaxAmount().toString());

            totalsTable.addCell(new Paragraph("Total Due:").setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            totalsTable.addCell(new Paragraph("$" + taxResult.getTotalAmount().toString()).setBold());

            document.add(totalsTable);

            // Footer
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Thank you for your business.")
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setItalic());

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF invoice", e);
            throw new RuntimeException("Failed to generate PDF invoice", e);
        }
    }
}
