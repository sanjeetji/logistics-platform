markdown
# Tracking Service

Real-time GPS tracking and location management.

## Purpose
- Real-time driver location tracking
- Geofence entry/exit detection
- Route deviation alerts
- ETA prediction and updates
- Historical trip playback

## Features
- WebSocket for real-time updates
- Batch location processing
- Battery optimization for mobile apps
- Location history storage
- Geospatial indexing and queries

## API Endpoints
POST /api/v1/tracking/location - Update location
GET /api/v1/tracking/{orderId} - Get order tracking
GET /api/v1/tracking/driver/{driverId} - Get driver location
POST /api/v1/tracking/geofence - Create geofence
POST /api/v1/tracking/alert - Set tracking alert
GET /api/v1/tracking/history/{entityId} - Location history
WS /ws/tracking/{orderId} - WebSocket for real-time
POST /api/v1/tracking/batch - Batch location update

text

## Location Data
- GPS coordinates (lat, long)
- Speed and direction
- Battery level
- Network connectivity
- Timestamp with timezone
- Accuracy level