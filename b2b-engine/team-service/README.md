markdown
# Team Service

Driver and staff management with skills and geofences.

## Purpose
- Driver onboarding and management
- Skill and certification tracking
- Performance monitoring
- Geofence management
- Shift and schedule management

## Driver Categories
- Full-time drivers
- Part-time drivers
- Contract drivers
- Owner operators
- Fleet partners

## Skills and Certifications
- Vehicle type certification
- Hazardous material handling
- Refrigerated transport
- Cash handling
- Customer service training
- Language proficiency

## API Endpoints
POST /api/v1/drivers - Add driver
GET /api/v1/drivers - List drivers
GET /api/v1/drivers/{id} - Get driver details
PUT /api/v1/drivers/{id} - Update driver
POST /api/v1/drivers/{id}/verify - Background verification
GET /api/v1/drivers/{id}/performance - Performance metrics
POST /api/v1/drivers/{id}/skills - Add skill
GET /api/v1/drivers/available - Available drivers
POST /api/v1/drivers/{id}/geofence - Set geofence
GET /api/v1/drivers/search - Search by criteria

text

## Geofence Management
- Delivery zone definition
- Restricted areas
- Preferred parking locations
- Pickup/dropoff points
- Warehouse zones