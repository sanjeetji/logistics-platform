### **9. `service-discovery/README.md`**
```markdown
# Service Discovery

Service registry and discovery using Eureka for the Logistics Platform.

## 🎯 Purpose
Provides service registration and discovery capabilities for all microservices in the platform, enabling dynamic service location and load balancing.

## ✨ Features
- **Service Registration**: Automatic registration of services on startup
- **Service Discovery**: Dynamic discovery of service instances
- **Health Monitoring**: Continuous health checking of registered services
- **Load Balancing**: Client-side load balancing support
- **High Availability**: Multiple Eureka server instances
- **Zone Awareness**: Support for multiple availability zones
- **REST API**: Full REST API for service discovery

## 🏗️ Architecture
Eureka Server Cluster
↑
Eureka Clients (All Services)
↑
Service Consumers
↑
Load Balancer (Ribbon/Spring Cloud LoadBalancer)

text

## ⚙️ Configuration

### Server Configuration
```yaml
# application.yml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
    prefer-ip-address: true
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
    eviction-interval-timer-in-ms: 60000
Client Configuration
yaml
# In each microservice
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
    healthcheck:
      enabled: true
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
    hostname: ${spring.cloud.client.hostname}
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
    metadata-map:
      zone: ${ZONE:primary}
      version: ${APP_VERSION:1.0.0}
📡 API Endpoints
text
GET    /eureka/apps                    # List all applications
GET    /eureka/apps/{appName}         # Get specific application
GET    /eureka/apps/{appName}/{instanceId} # Get instance
POST   /eureka/apps/{appName}         # Register instance
PUT    /eureka/apps/{appName}/{instanceId} # Renew lease
DELETE /eureka/apps/{appName}/{instanceId} # Deregister
GET    /actuator/health               # Health check
GET    /actuator/info                 # Info endpoint