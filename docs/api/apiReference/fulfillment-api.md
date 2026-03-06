# Fulfillment Module API Reference Guide

All endpoints for Order management, Dispatch, Routing, Geo, Parcels, Returns, and Location tracking.

## Base URL
`http://localhost:8080`

---

## 1. Order Controller (`/api/v1/orders`)
*Core order lifecycle management.*

### Get All Orders
* **Endpoint**: `GET /api/v1/orders`
* **Response**: `200 OK` — List of all orders.

### Get Order by ID
* **Endpoint**: `GET /api/v1/orders/{id}`
* **Response**: `200 OK` with `Order` object.

### Get Order by Order ID
* **Endpoint**: `GET /api/v1/orders/order/{orderId}`
* **Response**: `200 OK` with `Order` object.

### Create Order
* **Endpoint**: `POST /api/v1/orders`
* **Request Body**:
    ```json
    {
      "orderId": "ORD-001",
      "pickupAddress": "...",
      "deliveryAddress": "...",
      "customerId": 1,
      "packageWeight": 2.5,
      "serviceLevel": "STANDARD"
    }
    ```
* **Response**: `200 OK` with created `Order`.

### Update Order Status
* **Endpoint**: `PATCH /api/v1/orders/{id}/status?status=IN_TRANSIT`
* **Params**: `status` — `PENDING`, `ASSIGNED`, `PICKED_UP`, `IN_TRANSIT`, `DELIVERED`, `CANCELLED`
* **Response**: `200 OK` with updated `Order`.

### Assign Driver to Order
* **Endpoint**: `POST /api/v1/orders/{orderId}/assign`
* **Request Body**: `{"driverId": 1, "vehicleId": 2}`
* **Response**: `200 OK` — Driver assigned.

### Cancel Order
* **Endpoint**: `POST /api/v1/orders/{orderId}/cancel`
* **Request Body**: `{"reason": "Customer request"}`
* **Response**: `200 OK`.

### Mark Picked Up
* **Endpoint**: `POST /api/v1/orders/{orderId}/pickup`
* **Response**: `200 OK`.

### Mark In Transit
* **Endpoint**: `POST /api/v1/orders/{orderId}/transit`
* **Response**: `200 OK`.

### Mark Delivered
* **Endpoint**: `POST /api/v1/orders/{orderId}/deliver`
* **Request Body** *(optional)*: `{"photoUrl": "https://..."}`
* **Response**: `200 OK`.

### Update Delivery Preferences
* **Endpoint**: `PATCH /api/v1/orders/{orderId}/preferences`
* **Request Body**: `{"contactlessDelivery": true, "deliveryInstructions": "Leave at door"}`
* **Response**: `200 OK`.

### Get Order History
* **Endpoint**: `GET /api/v1/orders/{orderId}/history`
* **Response**: List of status change events with timestamps.

### Get Completed Orders (Date Range)
* **Endpoint**: `GET /api/v1/orders/completed?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59`
* **Response**: List of completed orders in the date range.

### Get Demand Count
* **Endpoint**: `GET /api/v1/orders/demand`
* **Response**: Integer count of current demand.

---

## 2. Dispatch Controller (`/api/v1/dispatch`)
*Driver assignment and dispatch automation.*

### Find Best Driver
* **Endpoint**: `POST /api/v1/dispatch/find-driver`
* **Request Body**:
    ```json
    {
      "orderId": "ORD-001",
      "pickupLat": 28.6139,
      "pickupLng": 77.2090,
      "serviceLevel": "STANDARD"
    }
    ```
* **Response**: `200 OK` with best matching `DriverScore`.

### Assign Order to Driver
* **Endpoint**: `POST /api/v1/dispatch/assign?orderId=ORD-001&driverId=1&vehicleId=2`
* **Response**: `200 OK` with `DispatchAssignment`.

### Auto Dispatch
* **Endpoint**: `POST /api/v1/dispatch/auto-dispatch`
* **Request Body**: Same as find-driver request.
* **Response**: `200 OK` with `DispatchJob` (fully automated assignment).

### Get Assignment by Order
* **Endpoint**: `GET /api/v1/dispatch/assignment/{orderId}`
* **Response**: `200 OK` with `DispatchAssignment`.

### Cancel Assignment
* **Endpoint**: `POST /api/v1/dispatch/cancel/{orderId}`
* **Response**: `200 OK` — Assignment cancelled.

---

## 3. Routing Controller (`/api/v1/routing`)
*Route optimization and batch processing using VRP Solver.*

### Optimize Route
* **Endpoint**: `POST /api/v1/routing/optimize`
* **Request Body**:
    ```json
    {
      "tenantId": "tenant-1",
      "depot": {"id": "depot-1", "latitude": 28.6139, "longitude": 77.2090},
      "orders": [],
      "vehicles": []
    }
    ```
* **Response**: `200 OK` with optimized route result.

### Batch Optimize
* **Endpoint**: `POST /api/v1/routing/batch-optimize`
* **Query Params**: `tenantId`, `lat`, `lon`, `radius`
* **Response**: `200 OK` — Batch optimization job started.

---

## 4. Geo Controller (`/api/v1/geo`)
*Geocoding and geospatial services.*

### Geocode Address
* **Endpoint**: `GET /api/v1/geo/geocode?address=Connaught+Place+Delhi`
* **Response**: `200 OK` with `{lat, lng}`.

### Reverse Geocode
* **Endpoint**: `GET /api/v1/geo/reverse?lat=28.6139&lng=77.2090`
* **Response**: `200 OK` with address string.

### Calculate Distance
* **Endpoint**: `GET /api/v1/geo/distance?fromLat=28.6&fromLng=77.2&toLat=28.7&toLng=77.3`
* **Response**: Distance in km.

---

## 5. Location Hub Controller (`/api/v1/location`)
*Real-time driver location tracking.*

### Update Driver Location
* **Endpoint**: `POST /api/v1/location/update`
* **Request Body**: `{"driverId": 1, "latitude": 28.61, "longitude": 77.21, "timestamp": "..."}`
* **Response**: `200 OK`.

### Get Driver Location
* **Endpoint**: `GET /api/v1/location/driver/{driverId}`
* **Response**: Last known `{lat, lng, timestamp}`.

---

## 6. Returns Controller (`/api/v1/returns`)
*Reverse logistics and return management.*

### Create Return Request
* **Endpoint**: `POST /api/v1/returns`
* **Request Body**: `{"orderId": "ORD-001", "reason": "Damaged", "items": [...]}`
* **Response**: `201 Created` with return tracking ID.

### Get Return by ID
* **Endpoint**: `GET /api/v1/returns/{id}`
* **Response**: `200 OK` with return details.

---

## 7. Parcel Controller (`/api/v1/parcels`)
*Parcel-level tracking within an order.*

### Get Parcels for Order
* **Endpoint**: `GET /api/v1/parcels/order/{orderId}`
* **Response**: List of parcels.

### Update Parcel Status
* **Endpoint**: `PATCH /api/v1/parcels/{parcelId}/status?status=DELIVERED`
* **Response**: `200 OK`.

---

## 8. System Health
* **Endpoint**: `GET /actuator/health`
* **Response**: `{"status": "UP"}`
