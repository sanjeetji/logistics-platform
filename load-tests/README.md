# Load Testing Suite

Comprehensive load testing for the Logistics Platform using Gatling 3.10.5.

## Overview

This module contains load tests for validating platform performance, scalability, and identifying bottlenecks. Tests cover individual services and end-to-end workflows.

## Test Scenarios

### 1. OrderServiceLoadTest
**Target**: 1000+ orders/hour, p95 < 500ms

- **Create Orders**: Ramp to 100 users, sustain 50 TPS
- **Retrieve Orders**: Test caching performance
- **Update Status**: Order lifecycle transitions
- **List Orders**: Pagination and filtering

### 2. CustomerServiceLoadTest
**Target**: Cache hit ratio > 80%, p95 < 300ms

- **Get Customer by ID**: Cache hit/miss validation
- **Get Addresses**: Repeated reads for cache testing
- **Update Profile**: Cache eviction testing
- **Add Address**: Cache invalidation verification

### 3. FleetServiceLoadTest
**Target**: 1000 drivers streaming location, p95 < 400ms

- **Update Location**: Simulate 1000 drivers with continuous updates
- **Find Nearby Drivers**: Geospatial radius queries
- **Geofencing**: Polygon-based area searches
- **Driver Status**: Status updates and retrieval

### 4. EndToEndLoadTest
**Target**: Complete lifecycle < 2000ms (p99)

- **Complete Order Lifecycle**: Create → Assign → Deliver → Pay → Rate
- **Cross-Service Integration**: Tests all services working together
- **Concurrent Orders**: Stress test with high concurrency

## Prerequisites

### Running Services
Ensure all services are running before executing load tests:

```bash
# Start infrastructure
cd docker
docker-compose up -d postgres redis kafka

# Start services (in separate terminals or use docker-compose)
cd platform-core/order-service && mvn spring-boot:run
cd platform-core/customer-service && mvn spring-boot:run
cd platform-core/fleet-service && mvn spring-boot:run
cd platform-core/payment-service && mvn spring-boot:run
cd shared-services/rating-service && mvn spring-boot:run
```

### Service Ports
- Order Service: http://localhost:8081
- Customer Service: http://localhost:8082
- Fleet Service: http://localhost:8084
- Payment Service: http://localhost:8086
- Rating Service: http://localhost:8087

## Running Load Tests

### Individual Tests

```bash
cd load-tests

# Test Order Service
mvn gatling:test -Dgatling.simulationClass=simulations.OrderServiceLoadTest

# Test Customer Service (Redis caching validation)
mvn gatling:test -Dgatling.simulationClass=simulations.CustomerServiceLoadTest

# Test Fleet Service (geospatial performance)
mvn gatling:test -Dgatling.simulationClass=simulations.FleetServiceLoadTest

# Test End-to-End workflow
mvn gatling:test -Dgatling.simulationClass=simulations.EndToEndLoadTest
```

### Run All Tests

```bash
mvn gatling:test
```

## Viewing Results

Gatling generates HTML reports after each test execution:

```bash
# Reports are located in:
target/gatling/[simulation-name]-[timestamp]/index.html

# Open the latest report (macOS)
open target/gatling/$(ls -t target/gatling | head -1)/index.html
```

### Report Contents
- **Global Statistics**: Overall performance metrics
- **Response Time Distribution**: Percentiles (p50, p75, p95, p99)
- **Requests per Second**: Throughput over time
- **Response Time Over Time**: Performance trends
- **Active Users**: Concurrency levels

## Performance Targets

See [PerformanceMetrics.md](docs/PerformanceMetrics.md) for detailed SLA targets.

### Quick Reference

| Service | p95 Target | p99 Target | Success Rate |
|---------|------------|------------|--------------|
| Order Service | < 500ms | < 1000ms | > 99.9% |
| Customer Service | < 300ms | < 500ms | > 99.5% |
| Fleet Service | < 400ms | < 800ms | > 99.5% |
| End-to-End | < 1000ms | < 2000ms | > 95% |

## Monitoring During Tests

### Redis Cache Performance

```bash
# Monitor cache statistics
redis-cli INFO stats

# Watch cache hit ratio
watch -n 1 'redis-cli INFO stats | grep keyspace'

# Monitor memory usage
redis-cli INFO memory
```

### Database Connections

```bash
# PostgreSQL active connections
psql -U postgres -d logistics_orders -c "SELECT count(*) FROM pg_stat_activity;"

# Connection pool stats (check application logs)
tail -f platform-core/order-service/logs/application.log | grep "HikariPool"
```

### System Resources

```bash
# Docker container stats
docker stats

# JVM metrics (if JMX enabled)
jconsole localhost:9010  # order-service
jconsole localhost:9011  # customer-service
```

## Troubleshooting

### Test Failures

**Connection Refused**
- Ensure all services are running on correct ports
- Check `docker ps` for infrastructure services
- Verify service health: `curl http://localhost:8081/actuator/health`

**High Error Rate**
- Check service logs for exceptions
- Verify database connections
- Ensure Redis is running
- Check Kafka availability

**Slow Response Times**
- Monitor database query performance
- Check cache hit ratios
- Review connection pool utilization
- Verify no resource exhaustion (CPU, memory)

### Adjusting Load

Edit test files to modify load profiles:

```scala
// In OrderServiceLoadTest.scala
setUp(
  createOrderScenario.inject(
    rampUsers(50).during(1.minute),  // Reduce from 100
    constantUsersPerSec(25).during(3.minutes)  // Reduce from 50
  )
)
```

## Best Practices

1. **Warm-up Period**: Let services warm up for 1-2 minutes before peak load
2. **Gradual Ramp-up**: Use `rampUsers()` instead of immediate load
3. **Realistic Data**: Use varied test data to avoid cache pollution
4. **Baseline First**: Run tests before optimizations for comparison
5. **Isolate Tests**: Run one test at a time for accurate measurements
6. **Monitor Resources**: Watch CPU, memory, and database during tests
7. **Document Results**: Fill in [LoadTestResults.md](docs/LoadTestResults.md)

## Next Steps

After running tests:

1. **Analyze Reports**: Review Gatling HTML reports
2. **Document Results**: Update `docs/LoadTestResults.md`
3. **Identify Bottlenecks**: List performance issues
4. **Create Action Items**: Prioritize optimizations
5. **Retest**: Validate improvements after optimizations

## Additional Resources

- [Gatling Documentation](https://gatling.io/docs/current/)
- [Performance Metrics](docs/PerformanceMetrics.md)
- [Load Test Results](docs/LoadTestResults.md)
- [Platform Advancement Tasks](../PLATFORM_ADVANCEMENT_TASKS.md)
