# Streaming Analytics Service

Real-time analytics platform using Kafka Streams for aggregating platform metrics.

## Overview

This service provides real-time insights into platform operations by processing events from Kafka topics and aggregating key metrics using Kafka Streams. Metrics are stored in Redis and exposed via REST API and WebSocket for live dashboards.

## Features

- ✅ **Real-time Metrics Aggregation** - Kafka Streams processing
- ✅ **Order Metrics** - Orders by status, type, geographic breakdown
- ✅ **Driver Metrics** - Active drivers, utilization, performance
- ✅ **Revenue Metrics** - Real-time revenue tracking
- ✅ **SLA Metrics** - On-time delivery tracking
- ✅ **Dashboard API** - REST endpoints for all metrics
- ✅ **WebSocket Streaming** - Live updates every 10 seconds
- ✅ **Anomaly Detection** - Statistical anomaly detection
- ✅ **Redis Caching** - Low-latency metric retrieval

## Architecture

```
Kafka Topics → Kafka Streams → Redis → REST/WebSocket → Dashboard
   (Events)    (Aggregation)  (Storage)  (API)        (UI)
```

## Prerequisites

- Kafka 3.6+ running
- Redis 7.x running
- Java 21+
- Maven 3.9+

## Configuration

### application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    streams:
      application-id: streaming-analytics-service
      
  data:
    redis:
      host: localhost
      port: 6379

analytics:
  metrics:
    update-interval-seconds: 10
    retention-hours: 24
  anomaly:
    threshold-multiplier: 2.0
    min-samples: 10
```

## API Endpoints

### REST API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/analytics/realtime/orders` | GET | Current order metrics |
| `/api/analytics/realtime/drivers` | GET | Current driver metrics |
| `/api/analytics/realtime/revenue` | GET | Current revenue metrics |
| `/api/analytics/realtime/sla` | GET | Current SLA metrics |
| `/api/analytics/realtime/dashboard` | GET | All metrics combined |

### WebSocket

**Endpoint:** `/ws/analytics`

**Topic:** `/topic/metrics`

**Update Frequency:** Every 10 seconds

## Usage Examples

### REST API

```bash
# Get all metrics
curl http://localhost:8095/api/analytics/realtime/dashboard

# Get order metrics
curl http://localhost:8095/api/analytics/realtime/orders

# Get driver metrics
curl http://localhost:8095/api/analytics/realtime/drivers
```

### WebSocket Client (JavaScript)

```javascript
const socket = new SockJS('http://localhost:8095/ws/analytics');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    stompClient.subscribe('/topic/metrics', function(message) {
        const metrics = JSON.parse(message.body);
        console.log('Received metrics:', metrics);
        // Update dashboard UI
    });
});
```

## Metrics Details

### OrderMetrics
- Total orders, orders by status
- Orders by type (B2B, B2C, Express)
- Success/failure rates
- Orders per hour
- Geographic breakdown

### DriverMetrics
- Total/Available/Busy/Offline drivers
- Utilization rate
- Average delivery time
- Driver ratings
- Geographic distribution

### RevenueMetrics
- Total revenue (gross/net)
- Revenue by service type
- Transaction success rate
- Average order value
- Revenue by payment method

### SLAMetrics
- On-time delivery percentage
- SLA violations (critical/minor)
- Average delay
- Compliance by service tier

## Anomaly Detection

The service automatically detects:
- Order spikes (>2x normal)
- High failure rates (>10% and >2x normal)
- Driver availability drops
- Revenue anomalies

Anomalies generate alerts stored in Redis and can trigger notifications.

## Running the Service

### Local Development

```bash
# Start dependencies
docker-compose up -d kafka redis

# Run the service
mvn spring-boot:run
```

### Docker

```bash
docker build -t streaming-analytics-service .
docker run -p 8095:8095 \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e REDIS_HOST=redis \
  streaming-analytics-service
```

## Monitoring

- Health check: `http://localhost:8095/actuator/health`
- Metrics: `http://localhost:8095/actuator/metrics`
- Prometheus: `http://localhost:8095/actuator/prometheus`

## Development Status

**Status:** ✅ Core Features Complete

**Completed:**
- Kafka Streams topology for order aggregation
- All metric models
- Redis storage service
- REST API endpoints
- WebSocket broadcast
- Anomaly detection

**Pending:**
- Additional stream processors (driver, revenue, SLA)
- Alert notification integration
- Comprehensive testing
- Stream processing optimization

## Next Steps

1. Implement remaining Kafka Streams processors
2. Integrate with notification-service for alerts
3. Add comprehensive unit and integration tests
4. Optimize stream processing windows
5. Create Grafana dashboards for metrics visualization
