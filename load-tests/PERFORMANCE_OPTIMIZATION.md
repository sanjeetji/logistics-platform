# Performance Optimization Implementation Guide

## Overview
This document outlines performance optimizations to implement based on anticipated load test results.

## 1. Redis Caching Strategy

### Hot Data Caching
**Services to Cache:**
- **Order Service:** Active order status, recent orders
- **Fleet Service:** Driver locations (1-minute TTL), driver status
- **Pricing Service:** Price calculation results, zone configs
- **Customer Service:** Customer profiles, preferences

### Implementation Steps

#### 1.1 Add Redis Dependencies
Already added in parent POM. Services inherit automatically.

#### 1.2 Configure Redis in Service
Add to `application.yml`:
```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
      timeout: 2000ms
      
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutes default
      cache-null-values: false
```

#### 1.3 Enable Caching in Services
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
            );
    }
}
```

#### 1.4 Apply Caching Annotations
```java
@Cacheable(value = "orders", key = "#orderId")
public Order getOrder(String orderId) { ... }

@CacheEvict(value = "orders", key = "#orderId")
public void updateOrderStatus(String orderId, OrderStatus status) { ... }

@Cacheable(value = "driverLocations", key = "#driverId")
public DriverLocation getDriverLocation(String driverId) { ... }
```

---

## 2. Database Connection Pooling

### HikariCP Optimization (Already Default in Spring Boot)

Add to each service's `application.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # Max connections
      minimum-idle: 5              # Min idle connections
      connection-timeout: 30000     # 30s
      idle-timeout: 600000          # 10 minutes
      max-lifetime: 1800000         # 30 minutes
      auto-commit: true
      pool-name: HikariPool-${spring.application.name}
      
      # Performance tuning
      data-source-properties:
        cachePrepStmts: true
        prepStmtCacheSize: 250
        prepStmtCacheSqlLimit: 2048
        useServerPrepStmts: true
```

### Monitoring Connection Pool
```java
@Component
public class HikariMetrics {
    @Autowired
    private HikariDataSource dataSource;
    
    @Scheduled(fixedRate = 30000)
    public void logPoolMetrics() {
        HikariPoolMXBean poolProxy = dataSource.getHikariPoolMXBean();
        log.info("Pool Stats - Active: {}, Idle: {}, Waiting: {}, Total: {}",
            poolProxy.getActiveConnections(),
            poolProxy.getIdleConnections(),
            poolProxy.getThreadsAwaitingConnection(),
            poolProxy.getTotalConnections());
    }
}
```

---

## 3. Query Optimization

### 3.1 Add Database Indexes

**Order Service:**
```sql
CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_driver_status ON orders(driver_id, status);
CREATE INDEX idx_orders_status_created ON orders(status, created_at DESC);
```

**Fleet Service:**
```sql
CREATE INDEX idx_drivers_status ON drivers(status);
CREATE INDEX idx_locations_driver_timestamp ON driver_locations(driver_id, timestamp DESC);
CREATE INDEX idx_locations_geospatial ON driver_locations USING GIST(location);
```

**Order Events:**
```sql
CREATE INDEX idx_order_events_order_timestamp ON order_events(order_id, event_timestamp DESC);
CREATE INDEX idx_order_events_type ON order_events(event_type);
```

### 3.2 Use Projections for Queries
```java
// Instead of loading full entity
public interface OrderSummary {
    String getOrderId();
    OrderStatus getStatus();
    LocalDateTime getCreatedAt();
}

@VisitorQuery("SELECT o FROM Order o WHERE o.customerId = :customerId")
List<OrderSummary> findOrderSummariesByCustomer(@Param("customerId") String customerId);
```

### 3.3 Fix N+1 Queries
```java
// Add JOIN FETCH
@Query("SELECT o FROM Order o " +
       "LEFT JOIN FETCH o.items " +
       "LEFT JOIN FETCH o.customer " +
       "WHERE o.orderId = :orderId")
Optional<Order> findByIdWithDetails(@Param("orderId") String orderId);
```

### 3.4 Batch Operations
```java
@Modifying
@Query("UPDATE Order o SET o.status = :status WHERE o.orderId IN :orderIds")
void updateStatusBatch(@Param("status") OrderStatus status, @Param("orderIds") List<String> orderIds);
```

---

## 4. Async Processing

### 4.1 Enable Async Execution
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

### 4.2 Make Heavy Operations Async
```java
@Async("taskExecutor")
public CompletableFuture<Void> sendNotifications(Order order) {
    notificationService.sendOrderConfirmation(order);
    return CompletableFuture.completedFuture(null);
}

@Async("taskExecutor")
public CompletableFuture<RouteOptimization> optimizeRoute(List<Order> orders) {
    return CompletableFuture.completedFuture(routeOptimizer.optimize(orders));
}
```

---

## 5. HTTP Client Connection Pooling

### 5.1 Configure Feign Clients
Add to `application.yml`:
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: basic
  httpclient:
    enabled: true
    max-connections: 200
    max-connections-per-route: 50
    time-to-live: 900
    time-to-live-unit: seconds
```

### 5.2 RestTemplate Pool Configuration
```java
@Bean
public RestTemplate restTemplate() {
    PoolingHttpClientConnectionManager connectionManager = 
        new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(200);
    connectionManager.setDefaultMaxPerRoute(50);
    
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(5000)
        .setSocketTimeout(10000)
        .build();
    
    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setDefaultRequestConfig(requestConfig)
        .build();
    
    return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
}
```

---

## 6. Application-Level Optimizations

### 6.1 Use @Transactional Wisely
```java
// Read-only for queries (optimization hint)
@Transactional(readOnly = true)
public List<Order> getOrders() { ... }

// Keep transactions short
@Transactional
public void updateOrder(String orderId, UpdateRequest request) {
    Order order = orderRepository.findById(orderId).orElseThrow();
    order.update(request);
    orderRepository.save(order);
    // Event publishing outside transaction if possible
}
```

### 6.2 Optimize Jackson Serialization
```java
@JsonInclude(JsonInclude.Include.NON_NULL) // Don't serialize nulls
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore unknown fields
public class OrderDTO { ... }
```

### 6.3 Use Virtual Threads (Java 21)
```yaml
spring:
  threads:
    virtual:
      enabled: true
```

---

## 7. CDN for Static Assets (Future)

### Use Case
- API documentation (Swagger UI)
- Static images/icons
- Frontend assets

### Implementation
1. Set up CloudFront/CDN
2. Upload static assets to S3
3. Update URLs to point to CDN
4. Set appropriate cache headers

---

## Implementation Priority

### Phase 1: Immediate (P0)
1. ✅ Database connection pooling configuration
2. ✅ Critical index creation (orders, fleet)
3. ✅ Redis configuration for hot data

### Phase 2: High Priority (P1)
4. Query optimization (projections, N+1 fixes)
5. Async processing for notifications/heavy ops
6. HTTP client pooling

### Phase 3: Medium Priority (P2)
7. Advanced caching strategies
8. Virtual threads enablement
9. Batch operations

---

## Monitoring & Validation

After implementing optimizations, monitor:
- **Database:** Query execution times, connection pool metrics
- **Redis:** Hit/miss ratio, memory usage
- **Application:** Response times, throughput
- **JVM:** Heap usage, GC pauses

Re-run load tests to validate improvements.
