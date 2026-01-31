markdown
# Dispatch Service

Intelligent dispatch and route optimization for B2B logistics.

## Purpose
- Driver assignment optimization
- Route planning and optimization
- Real-time dispatch management
- Driver performance tracking
- SLA monitoring and compliance

## Algorithms
1. **Nearest Driver Assignment** - Based on proximity
2. **Load Balancing** - Even distribution among drivers
3. **Multi-stop Optimization** - Traveling salesman problem
4. **Traffic-aware Routing** - Real-time traffic data
5. **Capacity Planning** - Vehicle capacity optimization

## Features
- Real-time driver tracking
- Automatic reassignment on delays
- ETA prediction and updates
- Driver communication system
- Dispatch console for operators

## API Endpoints
POST /api/v1/dispatch/optimize - Optimize route
POST /api/v1/dispatch/assign - Assign driver
POST /api/v1/dispatch/reassign - Reassign driver
GET /api/v1/dispatch/drivers - Available drivers
GET /api/v1/dispatch/queued - Queued orders
POST /api/v1/dispatch/bulk - Bulk dispatch
GET /api/v1/dispatch/metrics - Dispatch metrics
POST /api/v1/dispatch/auto - Auto-dispatch toggle

text

## Integration
- Order Service: Order details
- Fleet Service: Vehicle capacity
- Geo Service: Routing and distances
- Team Service: Driver skills and availability