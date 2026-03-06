# Fleet Module API Reference Guide

All endpoints for Driver management, Vehicles, Shifts, Teams, Compliance, Ratings, and Geospatial features.

## Base URL
`http://localhost:8080`

---

## 1. Driver Controller (`/api/v1/drivers`)
*Driver CRUD and status management.*

### Get All Drivers
* **Endpoint**: `GET /api/v1/drivers`
* **Response**: `200 OK` — List of all `DriverDto`.

### Get Available Drivers
* **Endpoint**: `GET /api/v1/drivers/available`
* **Response**: `200 OK` — Drivers currently available for assignment.

### Get Driver by ID
* **Endpoint**: `GET /api/v1/drivers/{id}`
* **Response**: `200 OK` with `DriverDto`.

### Create Driver
* **Endpoint**: `POST /api/v1/drivers`
* **Request Body**:
    ```json
    {
      "firstName": "Rahul",
      "lastName": "Sharma",
      "email": "rahul@example.com",
      "phone": "+91-9876543210",
      "licenseNumber": "DL-1234567890",
      "vehicleType": "BIKE"
    }
    ```
* **Response**: `200 OK` with created `DriverDto`.

### Update Driver
* **Endpoint**: `PUT /api/v1/drivers/{id}`
* **Request Body**: Updated `DriverDto` fields.
* **Response**: `200 OK` with updated `DriverDto`.

### Update Driver Status
* **Endpoint**: `PATCH /api/v1/drivers/{id}/status?status=AVAILABLE&reason=Shift started`
* **Params**:
    * `status`: `AVAILABLE`, `BUSY`, `OFFLINE`, `ON_BREAK`, `SUSPENDED`
    * `reason` *(optional)*: Reason for status change.
* **Response**: `200 OK` with updated `DriverDto`.

### Delete Driver
* **Endpoint**: `DELETE /api/v1/drivers/{id}`
* **Response**: `200 OK`.

---

## 2. Vehicle Controller (`/api/v1/vehicles`)
*Fleet vehicle management.*

### Get All Vehicles
* **Endpoint**: `GET /api/v1/vehicles`
* **Response**: `200 OK` — List of all `VehicleDto`.

### Get Active Vehicles
* **Endpoint**: `GET /api/v1/vehicles/active`
* **Response**: `200 OK` — Only vehicles currently in active/deployed state.

### Get Vehicle by ID
* **Endpoint**: `GET /api/v1/vehicles/{id}`
* **Response**: `200 OK` with `VehicleDto`.

### Create Vehicle
* **Endpoint**: `POST /api/v1/vehicles`
* **Request Body**:
    ```json
    {
      "registrationNumber": "DL-01-AB-1234",
      "vehicleType": "TRUCK",
      "capacity": 1000,
      "model": "Tata Ace",
      "year": 2022
    }
    ```
* **Response**: `200 OK` with created `VehicleDto`.

---

## 3. Shift Controller (`/api/v1/shifts`)
*Driver shift scheduling.*

### Get All Shifts
* **Endpoint**: `GET /api/v1/shifts`
* **Response**: List of all shifts.

### Get Shift by ID
* **Endpoint**: `GET /api/v1/shifts/{id}`
* **Response**: `200 OK` with shift details.

### Create Shift
* **Endpoint**: `POST /api/v1/shifts`
* **Request Body**: `{"driverId": 1, "startTime": "2024-01-01T08:00:00", "endTime": "2024-01-01T20:00:00"}`
* **Response**: `201 Created`.

### Update Shift
* **Endpoint**: `PUT /api/v1/shifts/{id}`
* **Response**: `200 OK`.

### Delete Shift
* **Endpoint**: `DELETE /api/v1/shifts/{id}`
* **Response**: `200 OK`.

---

## 4. Team Controller (`/api/v1/teams`)
*Driver team/group management.*

### Get All Teams
* **Endpoint**: `GET /api/v1/teams`
* **Response**: List of teams.

### Get Team by ID
* **Endpoint**: `GET /api/v1/teams/{id}`
* **Response**: `200 OK`.

### Create Team
* **Endpoint**: `POST /api/v1/teams`
* **Request Body**: `{"name": "North Zone Team", "managerId": 1}`
* **Response**: `201 Created`.

### Add Driver to Team
* **Endpoint**: `POST /api/v1/teams/{teamId}/drivers/{driverId}`
* **Response**: `200 OK`.

---

## 5. Rating Controller (`/api/v1/ratings`)
*Driver and delivery ratings.*

### Submit Rating
* **Endpoint**: `POST /api/v1/ratings`
* **Request Body**:
    ```json
    {
      "orderId": "ORD-001",
      "driverId": 1,
      "rating": 4.5,
      "comment": "Fast and professional delivery"
    }
    ```
* **Response**: `200 OK`.

### Get Driver Ratings
* **Endpoint**: `GET /api/v1/ratings/driver/{driverId}`
* **Response**: List of ratings with average score.

---

## 6. Compliance Controller (`/api/v1/compliance`)
*Driver document verification and compliance tracking.*

### Get Compliance Status
* **Endpoint**: `GET /api/v1/compliance/driver/{driverId}`
* **Response**: Compliance checklist (license, insurance, etc.).

### Upload Document
* **Endpoint**: `POST /api/v1/compliance/driver/{driverId}/documents`
* **Request**: `multipart/form-data` with file + documentType.
* **Response**: `200 OK` with document ID.

---

## 7. Geofence Controller (`/api/geofences`)
*Location-based zone management.*

### Create Geofence
* **Endpoint**: `POST /api/geofences`
* **Request Body**: `{"name": "Warehouse Zone", "type": "CIRCLE", "latitude": 28.6, "longitude": 77.2, "radiusInMeters": 500}`
* **Response**: `200 OK` with created geofence.

### Get All Geofences
* **Endpoint**: `GET /api/geofences`
* **Response**: List of geofences.

### Check if Point is Inside Geofence
* **Endpoint**: `GET /api/v1/geofences/{id}/check?lat=28.61&lng=77.21`
* **Response**: `{"inside": true}`.

---

## 8. Proof of Delivery (`/api/v1/pod`)
*Delivery confirmation with photo/signature.*

### Submit Proof of Delivery
* **Endpoint**: `POST /api/v1/pod`
* **Request**: `multipart/form-data` — photo + orderId + signature.
* **Response**: `200 OK` with POD record.

### Get POD for Order
* **Endpoint**: `GET /api/v1/pod/order/{orderId}`
* **Response**: POD details including photo URL.

---

## 9. Digital Twin Controller (`/api/v1/fleet/digital-twin`)
*Real-time exact 3D coordinates for Digital Twin consumers.*

### Get 3D Network Snapshot
* **Endpoint**: `GET /api/v1/fleet/digital-twin/snapshot`
* **Response**: `200 OK` with real-time 3D coordinate snapshot.

---

## 10. Fleet Search (`/api/v1/fleet/search`)
*Full-text and filter-based fleet search.*

### Search Drivers
* **Endpoint**: `GET /api/v1/fleet/search/drivers?q=Rahul&status=AVAILABLE`
* **Response**: Filtered driver list.

### Search Vehicles
* **Endpoint**: `GET /api/v1/fleet/search/vehicles?type=TRUCK&status=ACTIVE`
* **Response**: Filtered vehicle list.
