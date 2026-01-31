### **3. `b2b-engine/order-service/README.md`**
```markdown
# Order Service

Complex multi-stop B2B logistics order management system.

## 🎯 Purpose
Manages B2B logistics orders from creation to delivery, supporting complex multi-stop routes, bulk operations, and enterprise-grade features for business clients.

## ✨ Features
- **Multi-stop Orders**: Support for complex routes with multiple pickups and deliveries
- **Bulk Operations**: Create and manage hundreds of orders simultaneously
- **SLA Tracking**: Monitor and enforce service level agreements
- **Proof of Delivery**: Digital signatures, photos, and notes
- **Order Status Workflow**: Comprehensive state management
- **Advanced Search**: Filter and search across all order attributes
- **Export Capabilities**: CSV, Excel, PDF exports
- **Integration Ready**: REST API and webhook support

## 📊 Order Types
1. **Standard Delivery**: Single pickup, single delivery
2. **Multi-stop Delivery**: Multiple pickups and/or deliveries
3. **Scheduled Delivery**: Future date/time deliveries
4. **Same-day Delivery**: Urgent/express deliveries
5. **Warehouse Transfer**: Inter-warehouse transfers
6. **Return Orders**: Reverse logistics
7. **Bulk Shipments**: Large volume deliveries

## 🏗️ Architecture
Order Service → PostgreSQL (order data)
→ Redis (caching, queues)
→ RabbitMQ (events)
→ Dispatch Service (driver assignment)
→ Tracking Service (real-time updates)
→ Warehouse Service (inventory check)

text

## 📡 API Endpoints

### Order Management
POST /api/v1/orders # Create order
GET /api/v1/orders # List orders (with filters)
GET /api/v1/orders/{orderId} # Get order details
PUT /api/v1/orders/{orderId} # Update order
PATCH /api/v1/orders/{orderId}/status # Update order status
DELETE /api/v1/orders/{orderId} # Cancel order

text

### Bulk Operations
POST /api/v1/orders/bulk/create # Bulk order creation
POST /api/v1/orders/bulk/update # Bulk order update
POST /api/v1/orders/bulk/assign # Bulk assignment
POST /api/v1/orders/bulk/status # Bulk status update
GET /api/v1/orders/bulk/template # Download template
POST /api/v1/orders/bulk/import # Import from CSV/Excel

text

### Order Actions
POST /api/v1/orders/{orderId}/assign # Assign to driver
POST /api/v1/orders/{orderId}/track # Update tracking
POST /api/v1/orders/{orderId}/pod # Upload proof of delivery
GET /api/v1/orders/{orderId}/pod # Get proof of delivery
POST /api/v1/orders/{orderId}/notes # Add notes
POST /api/v1/orders/{orderId}/delay # Report delay
POST /api/v1/orders/{orderId}/reroute # Request reroute

text

### Search & Reports
GET /api/v1/orders/search # Advanced search
GET /api/v1/orders/export # Export orders
GET /api/v1/orders/metrics # Order metrics
GET /api/v1/orders/dashboard # Dashboard data
GET /api/v1/orders/reports/daily # Daily report
GET /api/v1/orders/reports/sla # SLA compliance report

text

## 🔄 Order Status Workflow
DRAFT → PENDING → CONFIRMED → ASSIGNED → PICKED_UP →
IN_TRANSIT → ARRIVED → DELIVERED → COMPLETED
↓ ↓ ↓
CANCELLED FAILED RETURNED

text

## 🗄️ Database Schema

### Orders Table
```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    order_type VARCHAR(20) DEFAULT 'STANDARD',
    status VARCHAR(20) DEFAULT 'DRAFT',
    priority VARCHAR(10) DEFAULT 'NORMAL',
    
    -- Pickup details
    pickup_address JSONB NOT NULL,
    pickup_contact_name VARCHAR(255),
    pickup_contact_phone VARCHAR(20),
    pickup_scheduled_at TIMESTAMP,
    pickup_completed_at TIMESTAMP,
    
    -- Delivery details
    delivery_address JSONB NOT NULL,
    delivery_contact_name VARCHAR(255),
    delivery_contact_phone VARCHAR(20),
    delivery_scheduled_at TIMESTAMP,
    delivery_completed_at TIMESTAMP,
    
    -- Items
    items JSONB DEFAULT '[]',
    total_weight DECIMAL(10,2),
    total_volume DECIMAL(10,2),
    declared_value DECIMAL(10,2),
    
    -- Assignment
    assigned_driver_id UUID,
    assigned_vehicle_id UUID,
    assigned_at TIMESTAMP,
    
    -- Financial
    amount DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'INR',
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    
    -- SLA
    sla_type VARCHAR(20),
    promised_delivery_time TIMESTAMP,
    actual_delivery_time TIMESTAMP,
    
    -- Metadata
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 1
);

CREATE INDEX idx_orders_tenant ON orders(tenant_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_driver ON orders(assigned_driver_id);
CREATE INDEX idx_orders_created ON orders(created_at DESC);
Order Items Table
sql
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES orders(id),
    sku VARCHAR(100),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    quantity INTEGER NOT NULL,
    unit_weight DECIMAL(10,2),
    unit_volume DECIMAL(10,2),
    unit_value DECIMAL(10,2),
    dimensions JSONB,
    handling_instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
⚙️ Configuration
yaml
server:
  port: 8083

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/logistics_orders
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 50

app:
  order:
    auto-confirm: true
    default-currency: INR
    max-items-per-order: 100
    bulk-operation:
      max-size: 1000
      timeout-seconds: 300
    sla:
      standard-hours: 48
      express-hours: 24
      urgent-hours: 4
🔄 Integration Points
With Dispatch Service
Send order for driver assignment

Receive assignment confirmations

Handle reassignment requests

With Tracking Service
Send real-time location updates

Receive ETA calculations

Geofence entry/exit notifications

With Warehouse Service
Check inventory availability

Reserve items for orders

Update stock levels post-delivery

With Billing Service
Create invoices on order completion

Handle payment status updates

Process refunds for cancelled orders

Event Publishing
java
// Key order events
OrderCreatedEvent
OrderStatusChangedEvent
OrderAssignedEvent
OrderDeliveredEvent
ProofOfDeliveryEvent
OrderCancelledEvent
🚀 Getting Started
Local Development
bash
# 1. Navigate to service
cd b2b-engine/order-service

# 2. Run service
mvn spring-boot:run

# 3. Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
Docker Deployment
yaml
# docker-compose service
order-service:
  image: logistics/order-service:1.0.0
  ports:
    - "8083:8083"
  environment:
    DB_URL: jdbc:postgresql://postgres:5432/logistics_orders
    REDIS_HOST: redis
    RABBITMQ_HOST: rabbitmq
  depends_on:
    - postgres
    - redis
    - rabbitmq
Environment Variables
bash
DB_URL=jdbc:postgresql://localhost:5432/logistics_orders
DB_USERNAME=postgres
DB_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
ORDER_SERVICE_PORT=8083
ENCRYPTION_KEY=your-encryption-key
🧪 Testing
bash
# Run all tests
mvn test

# Run integration tests
mvn verify -Pintegration

# Run with coverage
mvn jacoco:report

# Test specific scenarios
mvn test -Dtest=OrderServiceIntegrationTest
📊 Performance Considerations
Index heavily queried columns

Use pagination for list endpoints

Implement caching for frequent queries

Batch database operations

Use connection pooling

Monitor query performance

🔒 Security
Tenant isolation at database level

Row-level security policies

Input validation and sanitization

Rate limiting per tenant

Audit logging for all operations

Encryption for sensitive data

📈 Monitoring & Metrics
Orders created per hour/day

Average order value

SLA compliance rate

Order status distribution

Peak order times

Driver assignment success rate

🔧 Database Migrations
bash
# Using Flyway
mvn flyway:migrate

# Rollback last migration
mvn flyway:repair

# Check migration status
mvn flyway:info
📋 Bulk Order Template
csv
order_number,customer_id,pickup_address,delivery_address,items,priority
ORD-001,CUST-123,"123 Main St...","456 Oak St...","[{\"sku\":\"ITEM1\",\"qty\":2}]",NORMAL
ORD-002,CUST-456,"789 Pine St...","321 Elm St...","[{\"sku\":\"ITEM2\",\"qty\":1}]",HIGH
🚨 Error Handling
400: Invalid order data or validation errors

401: Authentication required

403: Tenant/user doesn't have permission

404: Order not found

409: Order in invalid state for operation

422: Business rule violation

429: Too many requests

500: Internal server error

📝 Notes
Order numbers are generated sequentially per tenant

Soft delete for audit compliance

Comprehensive audit trail

Support for partial order updates

Webhook support for external integrations

Rate limiting based on tenant plan