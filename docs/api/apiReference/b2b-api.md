# B2B & Inventory Module API Reference Guide

All endpoints for B2B Order management, Bulk Uploads, SLA monitoring, Shipments, Warehouse, and Inventory.

## Base URL
`http://localhost:8080`

---

## 1. B2B Order Controller (`/api/v1/b2b/orders`)
*Enterprise/business-to-business order creation with SLA tracking.*

### Create B2B Order
* **Endpoint**: `POST /api/v1/b2b/orders`
* **Request Body**:
    ```json
    {
      "clientId": 1,
      "externalOrderId": "EXT-ORD-001",
      "pickupAddress": "Warehouse, Noida",
      "deliveryAddress": "Client HQ, Gurgaon",
      "scheduledDeliveryDeadline": "2024-01-15T18:00:00",
      "serviceLevel": "EXPRESS",
      "items": [
        {"sku": "ITEM-001", "quantity": 10, "weight": 5.0}
      ]
    }
    ```
* **Response**: `200 OK` with `B2BOrder`.

### Sync B2B Order (from ERP/Integration)
* **Endpoint**: `POST /api/v1/b2b/orders/sync`
* **Request Body**: `B2BOrderDto` from external system.
* **Response**: `200 OK` — Order synced.

### Bulk Upload via CSV
* **Endpoint**: `POST /api/v1/b2b/orders/bulk/csv`
* **Request**: `multipart/form-data`
    * `file`: CSV file
    * `clientId`: Client ID (Long)
* **Response**: `200 OK` with `BulkUploadResult` (success count, failed count, errors).

### Bulk Upload via Excel
* **Endpoint**: `POST /api/v1/b2b/orders/bulk/excel`
* **Request**: `multipart/form-data`
    * `file`: `.xlsx` file
    * `clientId`: Client ID (Long)
* **Response**: `200 OK` with `BulkUploadResult`.

### Get B2B Order by ID
* **Endpoint**: `GET /api/v1/b2b/orders/{orderId}`
* **Response**: `200 OK` with `B2BOrder`.

### Get Orders for Client
* **Endpoint**: `GET /api/v1/b2b/orders/client/{clientId}`
* **Response**: List of all `B2BOrder` for the client.

### Get Orders by SLA Status
* **Endpoint**: `GET /api/v1/b2b/orders/sla/{status}`
* **Params**: `status` — `ON_TRACK`, `AT_RISK`, `BREACHED`
* **Response**: Filtered list of `B2BOrder`.

### Update Order Status
* **Endpoint**: `PUT /api/v1/b2b/orders/{orderId}/status?status=DELIVERED`
* **Params**: `status` — `PENDING`, `ASSIGNED`, `IN_TRANSIT`, `DELIVERED`, `FAILED`, `CANCELLED`
* **Response**: `200 OK` with updated `B2BOrder`.

### Reschedule Order
* **Endpoint**: `PUT /api/v1/b2b/orders/{orderId}/reschedule?newDeadline=2024-01-20T18:00:00`
* **Response**: `200 OK` with rescheduled `B2BOrder`.

### Get SLA Report
* **Endpoint**: `GET /api/v1/b2b/orders/sla-report?clientId=1&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59`
* **Response**:
    ```json
    {
      "totalOrders": 150,
      "onTrack": 130,
      "atRisk": 15,
      "breached": 5,
      "slaComplianceRate": 86.7
    }
    ```

---

## 2. Recurring Order Controller (`/api/v1/b2b/recurring`)
*Scheduled/recurring order templates for regular B2B deliveries.*

### Create Recurring Order
* **Endpoint**: `POST /api/v1/b2b/recurring`
* **Request Body**:
    ```json
    {
      "clientId": 1,
      "frequency": "WEEKLY",
      "dayOfWeek": "MONDAY",
      "time": "09:00:00",
      "orderTemplate": {}
    }
    ```
* **Response**: `201 Created`.

### Get Recurring Orders for Client
* **Endpoint**: `GET /api/v1/b2b/recurring/client/{clientId}`
* **Response**: List of recurring order templates.

### Pause Recurring Order
* **Endpoint**: `PUT /api/v1/b2b/recurring/{id}/pause`
* **Response**: `200 OK`.

### Resume Recurring Order
* **Endpoint**: `PUT /api/v1/b2b/recurring/{id}/resume`
* **Response**: `200 OK`.

### Delete Recurring Order
* **Endpoint**: `DELETE /api/v1/b2b/recurring/{id}`
* **Response**: `200 OK`.

---

## 3. Shipment Controller (`/api/v1/shipments`)
*Multi-leg shipment tracking.*

### Create Shipment
* **Endpoint**: `POST /api/v1/shipments`
* **Request Body**: `{"orderId": "ORD-001", "carrier": "INTERNAL", "legs": [...]}`
* **Response**: `201 Created` with tracking number.

### Get Shipment by Tracking Number
* **Endpoint**: `GET /api/v1/shipments/track/{trackingNumber}`
* **Response**: Full shipment tracking timeline.

### Get Shipments for Order
* **Endpoint**: `GET /api/v1/shipments/order/{orderId}`
* **Response**: List of shipment legs.

### Update Shipment Status
* **Endpoint**: `PATCH /api/v1/shipments/{id}/status?status=IN_TRANSIT`
* **Response**: `200 OK`.

---

## 4. Warehouse Controller (`/api/v1/warehouses`)
*Warehouse facility management.*

### Get All Warehouses
* **Endpoint**: `GET /api/v1/warehouses`
* **Response**: List of warehouses.

### Get Warehouse by ID
* **Endpoint**: `GET /api/v1/warehouses/{id}`
* **Response**: `200 OK`.

### Create Warehouse
* **Endpoint**: `POST /api/v1/warehouses`
* **Request Body**: `{"name": "North Delhi Warehouse", "address": "...", "capacity": 10000}`
* **Response**: `201 Created`.

---

## 5. Warehouse Inventory Controller (`/api/v1/warehouses/{warehouseId}/inventory`)
*Stock management within warehouses.*

### Get Inventory
* **Endpoint**: `GET /api/v1/warehouses/{warehouseId}/inventory`
* **Response**: Current stock levels.

### Add Stock
* **Endpoint**: `POST /api/v1/warehouses/{warehouseId}/inventory`
* **Request Body**: `{"sku": "ITEM-001", "quantity": 100}`
* **Response**: `200 OK`.

### Reserve Stock (for order)
* **Endpoint**: `POST /api/v1/warehouses/{warehouseId}/inventory/reserve`
* **Request Body**: `{"sku": "ITEM-001", "quantity": 10, "orderId": "ORD-001"}`
* **Response**: `200 OK` or `409 Conflict` if insufficient stock.

---

## 6. B2B Inventory Controller (`/api/v1/b2b/inventory`)
*Client-specific inventory management.*

### Get Client Inventory
* **Endpoint**: `GET /api/v1/b2b/inventory/client/{clientId}`
* **Response**: Current inventory levels for client.

### Update Inventory
* **Endpoint**: `PUT /api/v1/b2b/inventory/client/{clientId}`
* **Request Body**: `{"sku": "ITEM-001", "quantity": 500}`
* **Response**: `200 OK`.

---

## 7. EDI Controller (`/api/v1/edi`)
*Electronic Data Interchange for ERP integration.*

### Receive EDI Order
* **Endpoint**: `POST /api/v1/edi/order`
* **Request Body**: EDI 850 format JSON.
* **Response**: `200 OK` with internal order ID.

### Send EDI shipment Confirmation
* **Endpoint**: `POST /api/v1/edi/shipment/confirm`
* **Request Body**: `{"orderId": "ORD-001", "trackingNumber": "TRK-123"}`
* **Response**: `200 OK`.
