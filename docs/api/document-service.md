# Document Service API Documentation

## Purpose
The Document Service manages file uploads, storage, and retrieval (e.g., Proof of Delivery, Invoices, User IDs). It uses MinIO/S3 as the storage provider.

## Access Details
- **Base URL**: `http://localhost:8096` (Check discovery for exact port)
- **Gateway URL**: `http://localhost:8080/api/documents`
- **Swagger UI**: [http://localhost:8096/swagger-ui/index.html](http://localhost:8096/swagger-ui/index.html)

## Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/documents/upload` | Upload a file and get a document ID |
| GET | `/api/documents/{docId}` | Get metadata and download link |
| DELETE | `/api/documents/{docId}` | Remove a document |
| GET | `/api/documents/tenant/{tenantId}` | List documents for a business |

## Postman Collection
Refer to the global collection: `docs/postman/Logistics-Platform-API.postman_collection.json`
Folder: `Shared - Document Service`
