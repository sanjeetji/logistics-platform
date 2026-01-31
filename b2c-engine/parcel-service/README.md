markdown
# Parcel Service

Simple A→B parcel delivery for B2C customers.

## Purpose
- Quick parcel booking
- Instant price calculation
- Package dimension validation
- Delivery time estimation
- Customer portal integration

## Package Types
- Document (up to 500g)
- Small parcel (up to 5kg)
- Medium parcel (5-20kg)
- Large parcel (20-50kg)
- Extra large (50kg+)
- Fragile items
- Perishable items

## API Endpoints
POST /api/v1/parcels - Create parcel booking
GET /api/v1/parcels - List parcels
GET /api/v1/parcels/{id} - Get parcel details
POST /api/v1/parcels/{id}/cancel - Cancel parcel
POST /api/v1/parcels/{id}/track - Update tracking
GET /api/v1/parcels/{id}/quote - Get price quote
POST /api/v1/parcels/{id}/schedule - Schedule pickup
POST /api/v1/parcels/bulk - Bulk parcel creation
GET /api/v1/parcels/search - Search parcels

text

## Pricing Model
- Base fare + per km charge
- Weight-based surcharge
- Dimension-based charge
- Time-based pricing (peak/off-peak)
- Priority delivery premium