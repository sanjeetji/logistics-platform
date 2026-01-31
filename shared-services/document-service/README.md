markdown
# Document Service

Document generation, storage, and management.

## Purpose
- Proof of Delivery (POD) generation
- Invoice and receipt creation
- Label and barcode generation
- Document storage and retrieval
- Digital signature management

## Document Types
1. **Proof of Delivery** - Customer signature, photo
2. **Invoices** - Customer billing
3. **Waybills** - Shipping documents
4. **Labels** - Barcode/QR code labels
5. **Reports** - PDF reports
6. **Certificates** - Delivery certificates

## API Endpoints
POST /api/v1/documents/pod - Generate POD
POST /api/v1/documents/invoice - Generate invoice
POST /api/v1/documents/waybill - Generate waybill
GET /api/v1/documents/{id} - Get document
PUT /api/v1/documents/{id} - Update document
DELETE /api/v1/documents/{id} - Delete document
POST /api/v1/documents/sign - Add digital signature
GET /api/v1/documents/search - Search documents
POST /api/v1/documents/template - Create template
POST /api/v1/documents/barcode - Generate barcode

text

## Storage Backend
- AWS S3 (primary)
- Google Cloud Storage (backup)
- Local storage (development)
- Document metadata in PostgreSQL