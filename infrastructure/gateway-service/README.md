markdown
# API Gateway Service

Central API gateway with routing, security, and rate limiting.

## Purpose
- Request routing to microservices
- Authentication and authorization
- Rate limiting and throttling
- Request/response transformation
- API versioning
- Circuit breaker pattern
- Request logging and monitoring

## Features
- JWT validation
- Role-based route access
- Request/response logging (ELK)
- API documentation aggregation (Swagger)
- CORS configuration
- Load balancing
- Health check aggregation

## Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: auth-service
        uri: lb://auth-service
        predicates:
          - Path=/api/v1/auth/**
        filters:
          - StripPrefix=1
          - name: CircuitBreaker
            args:
              name: authService
              fallbackUri: forward:/fallback/auth
Security
API Key authentication for external clients

JWT validation for internal services

IP whitelisting

DDoS protection

Request size limits

Monitoring
Request rate metrics

Error rate tracking

Response time percentiles

Circuit breaker status

text

## 🚚 **B2B ENGINE**

### **6. `b2b-engine/order-service/README.md`**
```markdown
# Order Service

Complex multi-stop B2B logistics order management.

## Purpose
- B2B order creation and management
- Multi-stop route optimization
- Order tracking and status updates
- Proof of delivery (POD) management
- Returns and reverse logistics

## Order Types
1. **Standard Delivery** - Fixed time delivery
2. **Scheduled Delivery** - Future date/time
3. **Same-day Delivery** - Urgent delivery
4. **Multi-stop Delivery** - Multiple pickups/dropoffs
5. **Warehouse Transfer** - Inter-warehouse transfer

## API Endpoints
POST /api/v1/orders - Create order
GET /api/v1/orders - List orders
GET /api/v1/orders/{id} - Get order details
PUT /api/v1/orders/{id} - Update order
POST /api/v1/orders/{id}/cancel - Cancel order
POST /api/v1/orders/{id}/assign - Assign to driver
POST /api/v1/orders/{id}/track - Update tracking
GET /api/v1/orders/{id}/pod - Get proof of delivery
POST /api/v1/orders/{id}/pod - Upload POD
GET /api/v1/orders/search - Search orders
POST /api/v1/orders/bulk - Bulk order creation

text

## Order Status Flow
PENDING → CONFIRMED → ASSIGNED → PICKED_UP → IN_TRANSIT →
DELIVERED → COMPLETED
↓
CANCELLED
↓
RETURNED

text

## Integration
- Dispatch Service: Driver assignment
- Warehouse Service: Inventory check
- Tracking Service: Real-time location
- Document Service: POD generation