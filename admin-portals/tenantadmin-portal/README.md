## 👥 **Tenant Admin Portal**

### **3. `admin-portals/tenantadmin-portal/README.md`**
```markdown
# Tenant Admin Portal

Client administration portal for tenant-specific operations and management.

## 🎯 Overview
The Tenant Admin Portal provides client administrators with tools to manage their logistics operations, including order management, driver monitoring, customer management, and performance analytics within their tenant scope.

## ✨ Key Features

### 📊 Tenant Dashboard
- **Order Overview**: Real-time order status and volumes
- **Performance Metrics**: Delivery success rates and SLA compliance
- **Revenue Analytics**: Tenant-specific revenue and cost analysis
- **Driver Performance**: Driver efficiency and customer ratings

### 📦 Order Management
- **Order Creation**: Create single or bulk orders
- **Order Tracking**: Real-time order tracking and status updates
- **Proof of Delivery**: Digital POD management and verification
- **Returns Management**: Handle returns and reverse logistics

### 🚚 Driver Management
- **Driver Onboarding**: Register and verify new drivers
- **Shift Scheduling**: Create and manage driver schedules
- **Performance Monitoring**: Track driver metrics and ratings
- **Payment Settlement**: Driver payments and commission management

### 👥 Customer Management
- **Customer Database**: Manage customer information and preferences
- **Address Book**: Saved delivery addresses and zones
- **Communication History**: Track customer interactions
- **Feedback Management**: Handle customer reviews and ratings

### 📈 Analytics & Reports
- **Performance Reports**: Delivery performance and efficiency
- **Financial Reports**: Revenue, costs, and profitability
- **Customer Reports**: Customer satisfaction and retention
- **Export Capabilities**: CSV, Excel, PDF exports

## 🏗️ Architecture

### Frontend Architecture
src/
├── components/ # Reusable UI components
│ ├── dashboard/ # Dashboard widgets
│ ├── orders/ # Order management components
│ ├── drivers/ # Driver management components
│ ├── customers/ # Customer management components
│ └── analytics/ # Analytics components
├── pages/ # Page components
│ ├── Dashboard/ # Tenant dashboard
│ ├── Orders/ # Order management
│ ├── Drivers/ # Driver management
│ ├── Customers/ # Customer management
│ ├── Analytics/ # Reports and analytics
│ └── Settings/ # Tenant settings
├── services/ # API service clients
│ ├── order.service.ts
│ ├── driver.service.ts
│ ├── customer.service.ts
│ └── analytics.service.ts
├── store/ # State management
│ ├── tenant/ # Tenant-specific state
│ ├── orders/ # Orders state
│ └── drivers/ # Drivers state
└── hooks/ # Custom React hooks

text

### Backend Architecture
com.logistics.tenantadmin
├── config/ # Configuration and security
├── controller/ # REST controllers
│ ├── OrderController.java
│ ├── DriverController.java
│ ├── CustomerController.java
│ └── AnalyticsController.java
├── service/ # Business logic services
│ ├── OrderService.java
│ ├── DriverService.java
│ ├── CustomerService.java
│ └── ReportService.java
├── repository/ # Data repositories
│ ├── OrderRepository.java
│ ├── DriverRepository.java
│ └── CustomerRepository.java
├── security/ # Tenant-aware security
│ ├── TenantContext.java
│ └── TenantFilter.java
└── dto/ # Data transfer objects

text

## 📡 API Endpoints

### Tenant Context
GET /api/v1/tenant/info # Get tenant information
PUT /api/v1/tenant/settings # Update tenant settings
GET /api/v1/tenant/stats # Tenant statistics
POST /api/v1/tenant/logo # Upload tenant logo

text

### Order Management
GET /api/v1/orders # List orders (with filters)
POST /api/v1/orders # Create order
GET /api/v1/orders/{id} # Get order details
PUT /api/v1/orders/{id} # Update order
DELETE /api/v1/orders/{id} # Cancel order
POST /api/v1/orders/bulk # Bulk order creation
GET /api/v1/orders/{id}/track # Track order
POST /api/v1/orders/{id}/pod # Upload proof of delivery
GET /api/v1/orders/export # Export orders
POST /api/v1/orders/{id}/assign # Assign to driver

text

### Driver Management
GET /api/v1/drivers # List drivers
POST /api/v1/drivers # Add driver
GET /api/v1/drivers/{id} # Get driver details
PUT /api/v1/drivers/{id} # Update driver
DELETE /api/v1/drivers/{id} # Remove driver
POST /api/v1/drivers/{id}/verify # Verify driver
GET /api/v1/drivers/{id}/stats # Driver statistics
POST /api/v1/drivers/{id}/assign # Assign order to driver
GET /api/v1/drivers/available # Available drivers
POST /api/v1/drivers/{id}/pay # Process driver payment

text

### Customer Management
GET /api/v1/customers # List customers
POST /api/v1/customers # Add customer
GET /api/v1/customers/{id} # Get customer details
PUT /api/v1/customers/{id} # Update customer
GET /api/v1/customers/{id}/orders # Customer orders
POST /api/v1/customers/{id}/address # Add address
GET /api/v1/customers/search # Search customers
POST /api/v1/customers/import # Import customers

text

### Analytics & Reports
GET /api/v1/analytics/dashboard # Dashboard metrics
GET /api/v1/analytics/orders # Order analytics
GET /api/v1/analytics/drivers # Driver performance
GET /api/v1/analytics/customers # Customer analytics
GET /api/v1/analytics/revenue # Revenue reports
POST /api/v1/analytics/reports # Generate custom report
GET /api/v1/analytics/export # Export analytics data

text

## ⚙️ Configuration

### Application Properties
```yaml
server:
  port: 8094
  servlet:
    context-path: /tenantadmin

spring:
  application:
    name: tenantadmin-portal
  datasource:
    url: jdbc:postgresql://localhost:5432/tenantadmin_portal_db
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD:admin123}
    hikari:
      maximum-pool-size: 15
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: ${SHOW_SQL:false}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_schema: ${TENANT_SCHEMA:public}
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://auth-service:8081

# Tenant Configuration
tenant:
  max-drivers: 100
  max-orders-per-day: 1000
  allowed-file-types: ['jpg', 'png', 'pdf', 'docx']
  notification-settings:
    email: true
    sms: true
    push: true

# Integration URLs
integration:
  order-service: http://order-service:8084
  dispatch-service: http://dispatch-service:8085
  tracking-service: http://tracking-service:8089
  billing-service: http://billing-service:8083
Tenant-aware Security
java
@Component
public class TenantFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Extract tenant ID from JWT token
        String tenantId = extractTenantIdFromToken(request);
        
        if (tenantId != null) {
            // Set tenant context
            TenantContext.setCurrentTenant(tenantId);
            
            try {
                filterChain.doFilter(request, response);
            } finally {
                // Clear tenant context
                TenantContext.clear();
            }
        } else {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Tenant not specified");
        }
    }
    
    private String extractTenantIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Decode JWT and extract tenant ID
            return jwtDecoder.decode(token).getClaim("tenantId");
        }
        return null;
    }
}
🚀 Getting Started
Prerequisites
bash
# Required services
Auth Service (running on :8081)
Order Service (running on :8084)
Tracking Service (running on :8089)
PostgreSQL (for tenant portal database)
Local Development
Backend Setup
bash
# Clone and navigate
cd admin-portals/tenantadmin-portal

# Set up database
docker run -d --name tenantadmin-db \
  -e POSTGRES_DB=tenantadmin_portal_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -p 5434:5432 \
  postgres:16-alpine

# Build and run
./mvnw clean install
./mvnw spring-boot:run

# The portal will be available at http://localhost:8094
Frontend Setup (React)
bash
cd frontend
npm install
npm start
# Frontend runs on http://localhost:3001
Docker Deployment
dockerfile
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM openjdk:21-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8094
ENTRYPOINT ["java", "-jar", "app.jar"]
bash
# Build and run
docker build -t logistics/tenantadmin-portal:latest .
docker run -p 8094:8094 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/tenantadmin_portal_db \
  logistics/tenantadmin-portal:latest
📊 Database Schema (Multi-tenant)
Tenant-specific Tables
sql
-- Orders table (tenant-scoped)
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    order_number VARCHAR(50) NOT NULL,
    customer_id UUID NOT NULL,
    driver_id UUID,
    status VARCHAR(20) DEFAULT 'PENDING',
    pickup_address JSONB NOT NULL,
    delivery_address JSONB NOT NULL,
    items JSONB DEFAULT '[]',
    amount DECIMAL(10,2),
    scheduled_pickup TIMESTAMP,
    scheduled_delivery TIMESTAMP,
    actual_delivery TIMESTAMP,
    proof_of_delivery JSONB,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, order_number)
);

-- Drivers table (tenant-scoped)
CREATE TABLE drivers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    driver_code VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    vehicle_type VARCHAR(50),
    vehicle_number VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    rating DECIMAL(3,2) DEFAULT 5.0,
    total_deliveries INT DEFAULT 0,
    total_earnings DECIMAL(10,2) DEFAULT 0,
    documents JSONB DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table (tenant-scoped)
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    customer_code VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    company_name VARCHAR(255),
    billing_address JSONB,
    shipping_addresses JSONB DEFAULT '[]',
    preferences JSONB DEFAULT '{}',
    total_orders INT DEFAULT 0,
    total_spent DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for tenant isolation
CREATE INDEX idx_orders_tenant ON orders(tenant_id);
CREATE INDEX idx_drivers_tenant ON drivers(tenant_id);
CREATE INDEX idx_customers_tenant ON customers(tenant_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_drivers_status ON drivers(status);
🔒 Security & Tenant Isolation
Tenant Data Isolation
java
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    @Query("SELECT o FROM Order o WHERE o.tenantId = :tenantId")
    Page<Order> findByTenantId(@Param("tenantId") String tenantId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.tenantId = :tenantId AND o.id = :id")
    Optional<Order> findByTenantIdAndId(@Param("tenantId") String tenantId, 
                                        @Param("id") UUID id);
    
    @Modifying
    @Query("DELETE FROM Order o WHERE o.tenantId = :tenantId AND o.id = :id")
    int deleteByTenantIdAndId(@Param("tenantId") String tenantId, 
                              @Param("id") UUID id);
}
Row-Level Security (PostgreSQL)
sql
-- Enable RLS
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE drivers ENABLE ROW LEVEL SECURITY;
ALTER TABLE customers ENABLE ROW LEVEL SECURITY;

-- Create policies
CREATE POLICY tenant_isolation_policy ON orders
    USING (tenant_id = current_setting('app.current_tenant_id'));

CREATE POLICY tenant_isolation_policy ON drivers
    USING (tenant_id = current_setting('app.current_tenant_id'));

CREATE POLICY tenant_isolation_policy ON customers
    USING (tenant_id = current_setting('app.current_tenant_id'));
📱 User Interface Components
Dashboard Widgets
typescript
const TenantDashboard = () => {
  const { tenantId } = useTenantContext();
  const { data: metrics } = useDashboardMetrics(tenantId);
  
  return (
    <div className="tenant-dashboard">
      <Row gutter={[16, 16]}>
        <Col span={6}>
          <StatCard
            title="Today's Orders"
            value={metrics.todayOrders}
            icon={<ShoppingCartOutlined />}
            trend={metrics.orderTrend}
          />
        </Col>
        <Col span={6}>
          <StatCard
            title="Active Drivers"
            value={metrics.activeDrivers}
            icon={<CarOutlined />}
          />
        </Col>
        <Col span={6}>
          <StatCard
            title="SLA Compliance"
            value={`${metrics.slaCompliance}%`}
            icon={<CheckCircleOutlined />}
            color={metrics.slaCompliance > 95 ? 'green' : 'orange'}
          />
        </Col>
        <Col span={6}>
          <StatCard
            title="Revenue (Today)"
            value={`₹${metrics.todayRevenue}`}
            icon={<DollarOutlined />}
            trend={metrics.revenueTrend}
          />
        </Col>
      </Row>
      
      <Row gutter={[16, 16]} style={{ marginTop: 20 }}>
        <Col span={12}>
          <OrderStatusChart orders={metrics.orderStatus} />
        </Col>
        <Col span={12}>
          <DriverPerformanceChart drivers={metrics.driverPerformance} />
        </Col>
      </Row>
      
      <Row style={{ marginTop: 20 }}>
        <Col span={24}>
          <RecentOrdersTable orders={metrics.recentOrders} />
        </Col>
      </Row>
    </div>
  );
};
Order Management Interface
typescript
const OrderManagement = () => {
  const [selectedOrder, setSelectedOrder] = useState(null);
  
  return (
    <div className="order-management">
      <OrderFilters onFilterChange={handleFilterChange} />
      
      <OrderDataGrid
        data={orders}
        onRowClick={setSelectedOrder}
        actions={{
          view: true,
          edit: true,
          cancel: true,
          track: true
        }}
        bulkActions={['export', 'assign', 'status-change']}
      />
      
      {selectedOrder && (
        <OrderDetailModal
          order={selectedOrder}
          onClose={() => setSelectedOrder(null)}
          onUpdate={handleOrderUpdate}
        />
      )}
      
      <FloatingActionButton
        icon={<PlusOutlined />}
        onClick={handleCreateOrder}
        tooltip="Create New Order"
      />
    </div>
  );
};
🔄 Real-time Features
Live Order Tracking
typescript
// WebSocket for real-time order updates
const useOrderTracking = (orderId: string) => {
  const [trackingData, setTrackingData] = useState<TrackingData | null>(null);
  
  useEffect(() => {
    const ws = new WebSocket(`ws://localhost:8094/ws/orders/${orderId}/track`);
    
    ws.onmessage = (event) => {
      const data: TrackingData = JSON.parse(event.data);
      setTrackingData(data);
    };
    
    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
    
    return () => ws.close();
  }, [orderId]);
  
  return trackingData;
};

// Map integration for live tracking
const OrderTrackingMap = ({ orderId }: { orderId: string }) => {
  const trackingData = useOrderTracking(orderId);
  
  return (
    <MapContainer center={[51.505, -0.09]} zoom={13}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      
      {trackingData && (
        <Marker position={[trackingData.latitude, trackingData.longitude]}>
          <Popup>
            Order: {orderId} <br />
            Status: {trackingData.status} <br />
            ETA: {trackingData.eta}
          </Popup>
        </Marker>
      )}
      
      <Polyline
        positions={trackingData?.route || []}
        color="blue"
        weight={3}
      />
    </MapContainer>
  );
};
📈 Analytics & Reporting
Report Generation
java
@Service
public class ReportService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    public Report generateDailyReport(String tenantId, LocalDate date) {
        // Get orders for the day
        List<Order> orders = orderRepository.findByTenantIdAndDate(tenantId, date);
        
        // Calculate metrics
        Report report = new Report();
        report.setTotalOrders(orders.size());
        report.setCompletedOrders(orders.stream()
            .filter(o -> "DELIVERED".equals(o.getStatus()))
            .count());
        report.setTotalRevenue(orders.stream()
            .mapToDouble(Order::getAmount)
            .sum());
        report.setAverageDeliveryTime(calculateAverageDeliveryTime(orders));
        
        // Driver performance
        List<DriverPerformance> driverPerformance = driverRepository
            .findDriverPerformance(tenantId, date);
        report.setDriverPerformance(driverPerformance);
        
        return report;
    }
    
    public byte[] exportReportToExcel(Report report) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Report");
        
        // Create headers
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Metric");
        headerRow.createCell(1).setCellValue("Value");
        
        // Add data
        int rowNum = 1;
        rowNum = addMetric(sheet, rowNum, "Total Orders", report.getTotalOrders());
        rowNum = addMetric(sheet, rowNum, "Completed Orders", report.getCompletedOrders());
        rowNum = addMetric(sheet, rowNum, "Total Revenue", report.getTotalRevenue());
        
        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
}
🚀 Performance Optimization
Caching Strategy
java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
            )
            .withInitialCacheConfigurations(Map.of(
                "tenant-metrics", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(1)),
                "order-list", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(2))
                    .prefixCacheNameWith("tenant:"),
                "driver-list", RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
            ))
            .build();
    }
    
    @Bean
    public KeyGenerator tenantAwareKeyGenerator() {
        return (target, method, params) -> {
            String tenantId = TenantContext.getCurrentTenant();
            return tenantId + ":" + method.getName() + ":" + 
                   Arrays.toString(params);
        };
    }
}
Database Query Optimization
java
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    @EntityGraph(attributePaths = {"customer", "driver"})
    @Query("SELECT o FROM Order o WHERE o.tenantId = :tenantId " +
           "AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersWithDetails(
        @Param("tenantId") String tenantId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable);
    
    @Query(value = """
        SELECT 
            DATE(o.created_at) as day,
            COUNT(*) as order_count,
            SUM(o.amount) as total_revenue,
            AVG(EXTRACT(EPOCH FROM (o.actual_delivery - o.created_at))/3600) as avg_delivery_hours
        FROM orders o
        WHERE o.tenant_id = :tenantId
        AND o.created_at >= :startDate
        GROUP BY DATE(o.created_at)
        ORDER BY day DESC
        LIMIT 30
        """, nativeQuery = true)
    List<Object[]> findDailyMetrics(
        @Param("tenantId") String tenantId,
        @Param("startDate") LocalDateTime startDate);
}
🧪 Testing
Test Configuration
java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class OrderControllerTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @Test
    @WithMockUser(roles = "TENANT_ADMIN")
    void shouldCreateOrder() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setPickupAddress("123 Main St");
        request.setDeliveryAddress("456 Oak St");
        
        mockMvc.perform(post("/api/v1/orders")
                .header("X-Tenant-ID", "test-tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderNumber").exists());
    }
    
    @Test
    void shouldNotAccessOtherTenantData() throws Exception {
        // Attempt to access order from different tenant
        mockMvc.perform(get("/api/v1/orders/other-tenant-order")
                .header("X-Tenant-ID", "current-tenant"))
            .andExpect(status().isNotFound());
    }
}
📦 Deployment
Docker Compose Configuration
yaml
version: '3.8'

services:
  tenantadmin-portal:
    build: .
    image: logistics/tenantadmin-portal:latest
    ports:
      - "8094:8094"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_URL=jdbc:postgresql://tenantadmin-db:5432/tenantadmin_portal_db
      - REDIS_HOST=redis
      - AUTH_SERVICE_URL=http://auth-service:8081
      - ORDER_SERVICE_URL=http://order-service:8084
    depends_on:
      - tenantadmin-db
      - redis
      - auth-service
      - order-service

  tenantadmin-db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: tenantadmin_portal_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    volumes:
      - tenantadmin_db_data:/var/lib/postgresql/data
    command: >
      postgres -c max_connections=100
               -c shared_buffers=256MB
               -c effective_cache_size=1GB

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru
    volumes:
      - redis_data:/data

volumes:
  tenantadmin_db_data:
  redis_data:
Kubernetes Deployment
yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tenantadmin-portal
  namespace: logistics-platform
  labels:
    app: tenantadmin-portal
    component: portal
    tier: admin
spec:
  replicas: 2
  selector:
    matchLabels:
      app: tenantadmin-portal
  template:
    metadata:
      labels:
        app: tenantadmin-portal
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8094"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      containers:
      - name: tenantadmin-portal
        image: logistics/tenantadmin-portal:${VERSION}
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8094
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_URL
          valueFrom:
            secretKeyRef:
              name: db-secrets
              key: tenantadmin-url
        - name: TENANT_CONTEXT_HEADER
          value: "X-Tenant-ID"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8094
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8094
          initialDelaySeconds: 30
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
      volumes:
      - name: config-volume
        configMap:
          name: tenantadmin-config
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            podAffinityTerm:
              labelSelector:
                matchExpressions:
                - key: app
                  operator: In
                  values:
                  - tenantadmin-portal
              topologyKey: kubernetes.io/hostname

---
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: tenantadmin-portal
  namespace: logistics-platform
spec:
  selector:
    app: tenantadmin-portal
  ports:
  - port: 80
    targetPort: 8094
    protocol: TCP
    name: http
  type: ClusterIP

---
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: tenantadmin-portal
  namespace: logistics-platform
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - tenantadmin.logistics-platform.com
    secretName: tenantadmin-tls
  rules:
  - host: tenantadmin.logistics-platform.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: tenantadmin-portal
            port:
              number: 80
📚 Documentation
API Documentation
Swagger UI: http://localhost:8094/swagger-ui.html

OpenAPI Spec: http://localhost:8094/v3/api-docs

Postman Collection: Available in docs/api/

User Guides
Getting Started Guide: Initial setup and configuration

Order Management Guide: Creating and managing orders

Driver Management Guide: Onboarding and managing drivers

Reporting Guide: Generating and understanding reports

Troubleshooting Guide: Common issues and solutions

Admin Training Materials
Video tutorials for common tasks

Quick reference cards

Best practices documentation

Security guidelines for tenant admins

🔧 Maintenance & Support
Monitoring
yaml
# Prometheus metrics
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: tenantadmin-portal
      environment: ${ENVIRONMENT:development}
  tracing:
    sampling:
      probability: 1.0
Backup Procedures
bash
#!/bin/bash
# backup-tenant-data.sh

# Database backup
TENANT_ID=$1
BACKUP_DIR="/backups/tenants/${TENANT_ID}"
DATE=$(date +%Y%m%d_%H%M%S)

# Backup tenant data
pg_dump -h localhost -U admin --schema=${TENANT_ID} \
  tenantadmin_portal_db > ${BACKUP_DIR}/db_${DATE}.sql

# Backup uploaded files
tar -czf ${BACKUP_DIR}/files_${DATE}.tar.gz /data/uploads/${TENANT_ID}/

# Sync to cloud storage
aws s3 sync ${BACKUP_DIR} s3://backup-bucket/tenants/${TENANT_ID}/

# Cleanup old backups (keep last 7 days)
find ${BACKUP_DIR} -type f -mtime +7 -delete
Disaster Recovery
Database Restoration: Restore from latest tenant backup

File Recovery: Restore uploaded files from backup

Configuration Recovery: Restore tenant configuration

Data Validation: Verify data consistency post-recovery

🤝 Contributing
Development Workflow
Create feature branch from develop

Implement changes with comprehensive tests

Update documentation as needed

Submit pull request for review

Address review comments

Merge after approval

Code Standards
Follow Google Java Style Guide

Use meaningful variable and method names

Write Javadoc for public APIs

Maintain 80%+ test coverage

Use meaningful commit messages

Security Guidelines
Never hardcode sensitive information

Validate all user inputs

Implement proper tenant isolation

Use prepared statements for database queries

Regular security dependency updates

📞 Support Channels
Technical Support
Email: tenantadmin-support@logistics-platform.com

Slack: #tenant-admin-portal channel

Documentation: Portal Documentation

Issue Tracking: GitHub Issues

Training & Onboarding
New Tenant Admin Onboarding: Weekly training sessions

Monthly Webinars: New features and best practices

Knowledge Base: FAQ and troubleshooting articles

Video Tutorials: Step-by-step guides

Maintenance Schedule
Weekly: Security updates and patches

Monthly: Performance optimization

Quarterly: Major feature releases

Annually: Platform upgrades

Empowering tenant administrators with powerful tools for efficient logistics management 🚚📊

text

## 🎯 **Key Differences Summary:**

| Aspect | Super Admin Portal | Tenant Admin Portal |
|--------|-------------------|---------------------|
| **Audience** | Platform owners | Client administrators |
| **Scope** | Platform-wide | Tenant-specific |
| **Permissions** | Full system access | Tenant-scoped access |
| **Data Access** | All tenants' data | Own tenant data only |
| **Configuration** | Platform settings | Tenant settings |
| **Monitoring** | System health | Tenant performance |
| **Security** | MFA required | Role-based access |

Both portals are essential for managing the Logistics Platform at different levels while maintaining proper security and data isolation boundaries.