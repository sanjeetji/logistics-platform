# APM Integration Guide

## Overview
This guide explains how to integrate Elastic APM Java agent into all services for application performance monitoring.

## Prerequisites
- ELK stack running (docker-compose-elk.yml)
- APM Server accessible at http://localhost:8200

## Implementation Steps

### 1. Download Elastic APM Agent

Download the latest APM agent JAR:
```bash
mkdir -p docker/apm-agent
cd docker/apm-agent
curl -o elastic-apm-agent.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.47.0/elastic-apm-agent-1.47.0.jar
```

### 2. Add APM Configuration to Services

For each service, add APM configuration to `application.yml`:

```yaml
elastic:
  apm:
    enabled: ${APM_ENABLED:true}
    server-url: ${APM_SERVER_URL:http://localhost:8200}
    service-name: ${spring.application.name}
    application-packages: com.logistics
    environment: ${SPRING_PROFILES_ACTIVE:development}
    log-level: INFO
   capture-body: all
    span-frames-min-duration: 5ms
```

### 3. Update Docker Compose (if running services in Docker)

Add APM agent to service containers:

```yaml
services:
  order-service:
    image: order-service:latest
    environment:
      JAVA_TOOL_OPTIONS: >
        -javaagent:/app/elastic-apm-agent.jar
        -Delastic.apm.service_name=order-service
        -Delastic.apm.server_urls=http://apm-server:8200
        -Delastic.apm.application_packages=com.logistics
        -Delastic.apm.environment=production
    volumes:
      - ../docker/apm-agent/elastic-apm-agent.jar:/app/elastic-apm-agent.jar:ro
```

### 4. Update Maven POM (Optional)

Add APM depende ncy to services (optional, agent attachment is preferred):

```xml
<dependency>
    <groupId>co.elastic.apm</groupId>
    <artifactId>apm-agent-attach</artifactId>
    <version>1.47.0</version>
</dependency>
```

### 5. Start Services with APM Agent

#### Local Development (JAR execution):
```bash
java -javaagent:docker/apm-agent/elastic-apm-agent.jar \
     -Delastic.apm.service_name=order-service \
     -Delastic.apm.server_urls=http://localhost:8200 \
     -Delastic.apm.application_packages=com.logistics \
     -jar platform-core/order-service/target/order-service-1.0.0-SNAPSHOT.jar
```

#### Using environment variable:
```bash
export JAVA_TOOL_OPTIONS="-javaagent:docker/apm-agent/elastic-apm-agent.jar -Delastic.apm.service_name=order-service -Delastic.apm.server_urls=http://localhost:8200 -Delastic.apm.application_packages=com.logistics"
java -jar order-service.jar
```

### 6. Verify APM Integration

1. Start a service with APM enabled
2. Generate some traffic (API calls, database queries)
3. Open Kibana: http://localhost:5601
4. Navigate to **Observability > APM**
5. Verify service appears in service list
6. Check transactions and traces

## Services to Instrument

Apply APM to all these services:

**Platform Core:**
- auth-service
- order-service
- fleet-service
- dispatch-service
- pricing-service
- customer-service
- driver-app-service
- wallet-service

**B2B Engine:**
- shipment-service
- warehouse-service
- inventory-service
- compliance-service

**Shared Services:**
- notification-service
- payment-service
- analytics-service
- audit-log-service
- search-service
- rating-service

**Gateway:**
- gateway-service

## Custom Instrumentation (Optional)

For custom spans and transactions:

```java
import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import co.elastic.apm.api.Transaction;

public class OrderService {
    
    public void processOrder(String orderId) {
        Transaction transaction = ElasticApm.currentTransaction();
        transaction.setLabel("orderId", orderId);
        
        Span span = transaction.startSpan("external", "http", "request");
        try {
            // Business logic
            span.setLabel("endpoint", "/api/orders");
        } finally {
            span.end();
        }
    }
}
```

## APM Configuration Options

Key configuration options:

- `elastic.apm.service_name` - Service identifier
- `elastic.apm.server_urls` - APM Server endpoint
- `elastic.apm.application_packages` - Packages to instrument
- `elastic.apm.environment` - Environment (dev/staging/prod)
- `elastic.apm.capture_body` - Capture request/response bodies
- `elastic.apm.transaction_sample_rate` - Sampling rate (0.0-1.0)
- `elastic.apm.capture_headers` - Capture HTTP headers
- `elastic.apm.log_level` - Agent log level

## Troubleshooting

### Service not appearing in APM
- Check APM Server is running: `curl http://localhost:8200`
- Verify agent JAR path is correct
- Check service logs for APM agent startup messages
- Ensure `application_packages` matches your package structure

### High overhead
- Reduce `transaction_sample_rate` (e.g., 0.1 for 10% sampling)
- Increase `span_frames_min_duration` to reduce detail
- Disable body capture: `capture_body=off`

### Missing transactions
- Check that endpoints are being called
- Verify sampling rate is not too low
- Check APM agent logs for errors

## Performance Impact

- Typical overhead: 1-3% CPU, minimal memory
- Sampling can reduce overhead further
- Production recommendation: 10-50% sampling rate

## Next Steps

1. Download APM agent JAR
2. Configure 2-3 critical services first (order-service, fleet-service, gateway-service)
3. Verify data appears in Kibana
4. Gradually roll out to remaining services
5. Create custom dashboards in Kibana
6. Set up alerts for performance degradation
