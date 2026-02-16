# Performance Metrics & SLA Targets

## Overview
This document defines the performance metrics, SLA targets, and baseline measurements for the logistics platform.

## Service-Level Objectives (SLOs)

### Response Time Targets

| Service | Endpoint Type | p50 | p95 | p99 | Max |
|---------|--------------|-----|-----|-----|-----|
| **Order Service** | Create Order | < 100ms | < 500ms | < 1000ms | < 2000ms |
| **Order Service** | Get Order (Cached) | < 50ms | < 200ms | < 400ms | < 800ms |
| **Order Service** | Update Status | < 80ms | < 300ms | < 600ms | < 1200ms |
| **Customer Service** | Get Customer (Cached) | < 30ms | < 100ms | < 200ms | < 500ms |
| **Customer Service** | Get Customer (Cache Miss) | < 100ms | < 300ms | < 500ms | < 1000ms |
| **Customer Service** | Update Profile | < 150ms | < 400ms | < 800ms | < 1500ms |
| **Fleet Service** | Update Location | < 50ms | < 200ms | < 400ms | < 800ms |
| **Fleet Service** | Find Nearby Drivers | < 100ms | < 400ms | < 800ms | < 1500ms |
| **Fleet Service** | Geofencing Query | < 150ms | < 500ms | < 1000ms | < 2000ms |
| **End-to-End** | Complete Order Lifecycle | < 500ms | < 1000ms | < 2000ms | < 4000ms |

### Throughput Targets

| Service | Operation | Target TPS | Peak TPS |
|---------|-----------|------------|----------|
| Order Service | Order Creation | 50 TPS | 100 TPS |
| Order Service | Order Retrieval | 200 TPS | 400 TPS |
| Customer Service | Profile Retrieval | 150 TPS | 300 TPS |
| Fleet Service | Location Updates | 500 TPS | 1000 TPS |
| Fleet Service | Spatial Queries | 100 TPS | 200 TPS |

### Availability & Reliability

- **Uptime**: 99.9% (< 43.2 minutes downtime/month)
- **Success Rate**: > 99.9% for all API calls
- **Error Rate**: < 0.1%

## Cache Performance Metrics

### Redis Cache Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| Cache Hit Ratio | > 80% | (Cache Hits / Total Requests) × 100 |
| Cache Response Time | < 10ms | p95 for cached reads |
| Cache Eviction Rate | < 5% | Evictions / Total Cache Operations |
| Memory Usage | < 80% | Redis memory utilization |

### Expected Cache Behavior

**Customer Service:**
- First request: ~200-300ms (database query)
- Cached request: ~20-50ms (Redis read)
- Cache improvement: **4-6x faster**

**Order Service:**
- First request: ~150-250ms
- Cached request: ~30-60ms
- Cache improvement: **3-5x faster**

## Database Performance

### Connection Pool Metrics

| Metric | Target | Alert Threshold |
|--------|--------|-----------------|
| Active Connections | < 50 | > 80 |
| Idle Connections | 10-20 | < 5 or > 40 |
| Wait Time | < 10ms | > 50ms |
| Pool Utilization | < 80% | > 90% |

### Query Performance

| Query Type | p95 Target | p99 Target |
|------------|------------|------------|
| Simple SELECT | < 10ms | < 20ms |
| JOIN Queries | < 50ms | < 100ms |
| Spatial Queries | < 100ms | < 200ms |
| Aggregations | < 200ms | < 400ms |

## System Resource Targets

### CPU Usage
- **Normal Load**: < 60%
- **Peak Load**: < 80%
- **Alert Threshold**: > 85%

### Memory Usage
- **JVM Heap**: < 75%
- **System Memory**: < 80%
- **Alert Threshold**: > 90%

### Network
- **Bandwidth**: < 70% utilization
- **Latency**: < 10ms inter-service
- **Packet Loss**: < 0.01%

## Load Test Scenarios

### Scenario 1: Normal Load
- **Duration**: 10 minutes
- **Users**: 100-200 concurrent
- **TPS**: 50-100
- **Expected**: All SLOs met

### Scenario 2: Peak Load
- **Duration**: 10 minutes
- **Users**: 500-1000 concurrent
- **TPS**: 200-500
- **Expected**: p95 within targets, p99 may exceed slightly

### Scenario 3: Stress Test
- **Duration**: 5 minutes
- **Users**: 1000-2000 concurrent
- **TPS**: 500-1000
- **Expected**: Identify breaking points

## Monitoring & Alerting

### Key Metrics to Monitor
1. **Response Time**: p50, p95, p99 for all endpoints
2. **Throughput**: Requests per second
3. **Error Rate**: 4xx and 5xx responses
4. **Cache Hit Ratio**: Redis cache performance
5. **Database Connections**: Pool utilization
6. **JVM Metrics**: Heap usage, GC pauses
7. **System Resources**: CPU, memory, disk I/O

### Alert Conditions
- Response time p95 > SLO for 5 minutes
- Error rate > 1% for 2 minutes
- Cache hit ratio < 70% for 10 minutes
- Database connection pool > 90% for 5 minutes
- CPU usage > 85% for 10 minutes
- Memory usage > 90% for 5 minutes

## Baseline Measurements

### Pre-Optimization Baseline
*(To be measured before Redis caching and async processing)*

| Service | Endpoint | p95 Response Time | TPS |
|---------|----------|-------------------|-----|
| Order Service | Create Order | TBD | TBD |
| Customer Service | Get Customer | TBD | TBD |
| Fleet Service | Update Location | TBD | TBD |

### Post-Optimization Target
*(Expected after Redis caching and async processing)*

| Service | Endpoint | p95 Response Time | Improvement |
|---------|----------|-------------------|-------------|
| Order Service | Create Order | < 500ms | Baseline |
| Customer Service | Get Customer | < 100ms | **4-6x faster** |
| Fleet Service | Update Location | < 200ms | Baseline |

## Test Execution Checklist

- [ ] All services running and healthy
- [ ] Redis cache running and configured
- [ ] PostgreSQL databases initialized
- [ ] Kafka running for event streaming
- [ ] Monitoring tools configured (optional: Prometheus, Grafana)
- [ ] Baseline metrics documented
- [ ] Load test scenarios configured
- [ ] Test data prepared

## Results Documentation

After running load tests, document:
1. **Actual vs Target Metrics**: Compare results to SLOs
2. **Bottlenecks Identified**: List performance issues
3. **Resource Utilization**: CPU, memory, database, cache
4. **Optimization Recommendations**: Actionable improvements
5. **Gatling HTML Reports**: Link to detailed reports
