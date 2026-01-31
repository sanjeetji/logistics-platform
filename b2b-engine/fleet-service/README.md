markdown
# Fleet Service

Vehicle and asset management for logistics fleet.

## Purpose
- Vehicle registration and management
- Maintenance scheduling
- Fuel tracking and management
- Insurance and documentation
- Asset tracking (GPS devices)

## Vehicle Types
- Two-wheeler (Bike/Scooter)
- Three-wheeler (Auto/Tempo)
- Four-wheeler (Car/Van)
- Truck (Small/Medium/Large)
- Electric Vehicles
- Refrigerated Vans

## API Endpoints
POST /api/v1/vehicles - Add vehicle
GET /api/v1/vehicles - List vehicles
GET /api/v1/vehicles/{id} - Get vehicle details
PUT /api/v1/vehicles/{id} - Update vehicle
DELETE /api/v1/vehicles/{id} - Remove vehicle
POST /api/v1/vehicles/{id}/maintenance - Schedule maintenance
GET /api/v1/vehicles/{id}/track - Track vehicle location
POST /api/v1/vehicles/{id}/assign - Assign to driver
GET /api/v1/vehicles/stats - Fleet statistics
POST /api/v1/vehicles/inspection - Record inspection

text

## Maintenance Tracking
- Periodic maintenance (KM based)
- Repair history
- Fuel efficiency tracking
- Tire replacement schedule
- Battery health monitoring