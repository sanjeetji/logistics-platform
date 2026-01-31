markdown
# Geo Service

Maps, geocoding, routing, and location intelligence.

## Purpose
- Address geocoding (address → coordinates)
- Reverse geocoding (coordinates → address)
- Route calculation and optimization
- Distance matrix calculation
- Location search and autocomplete

## Map Providers
- Google Maps API (primary)
- Mapbox (alternative)
- OpenStreetMap (fallback)
- Here Maps (enterprise)

## API Endpoints
POST /api/v1/geo/geocode - Geocode address
POST /api/v1/geo/reverse - Reverse geocode
POST /api/v1/geo/route - Calculate route
POST /api/v1/geo/distance - Distance matrix
GET /api/v1/geo/autocomplete - Address autocomplete
POST /api/v1/geo/isochrone - Generate isochrone
GET /api/v1/geo/search - Location search
POST /api/v1/geo/optimize - Optimize route
GET /api/v1/geo/places - Nearby places

text

## Route Optimization
- Fastest route
- Shortest distance
- Avoid tolls/highways
- Traffic-aware routing
- Multi-stop optimization