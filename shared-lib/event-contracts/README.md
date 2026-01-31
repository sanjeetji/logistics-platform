README.md for event-contracts:
markdown
# Event Contracts Library

Event schemas, DTOs, and contracts for event-driven architecture in the Logistics Platform.

## 🎯 Purpose
Defines standardized event structures, schemas, and contracts used for communication between microservices via message brokers (Kafka/RabbitMQ). Ensures consistent event format and versioning across the platform.

## 📁 Package Structure
com.logistics.sharedlib.events
├── dto/
│   ├── EventHeader.java            # Event metadata
│   ├── BaseEvent.java              # Base event class
│   ├── EventEnvelope.java          # Event wrapper
│   └── EventResponse.java          # Event response
├── enums/
│   ├── EventType.java              # Event types
│   ├── EventStatus.java            # Event status
│   ├── EventSource.java            # Event source
│   └── EventPriority.java          # Event priority
├── schemas/
│   ├── ShipmentEvent.java          # Shipment-related events
│   ├── OrderEvent.java             # Order-related events
│   ├── PaymentEvent.java           # Payment-related events
│   ├── NotificationEvent.java      # Notification events
│   ├── TrackingEvent.java          # Tracking events
│   └── AuditEvent.java             # Audit events
├── topics/
│   ├── KafkaTopics.java            # Kafka topic definitions
│   ├── ExchangeNames.java          # RabbitMQ exchanges
│   └── RoutingKeys.java            # RabbitMQ routing keys
└── avro/
├── shipment.avsc                # Avro schemas
├── order.avsc
└── payment.avsc

## 🛠️ Key Components

### Base Event Structure
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent<T> {
    
    @NotBlank
    private String eventId;
    
    @NotNull
    private EventType eventType;
    
    @NotNull
    private EventSource source;
    
    @NotBlank
    private String correlationId;
    
    @NotNull
    private ZonedDateTime timestamp;
    
    @NotNull
    private EventVersion version;
    
    @NotNull
    private T payload;
    
    @NotNull
    private EventHeader header;
    
    public abstract String getTopic();
    public abstract String getKey();
}
Event Header
java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventHeader {
    
    @NotBlank
    private String tenantId;
    
    @NotBlank
    private String userId;
    
    private String sessionId;
    
    @NotBlank
    private String applicationId;
    
    private String deviceId;
    
    private String ipAddress;
    
    @NotNull
    private Map<String, String> metadata;
    
    @NotNull
    private EventPriority priority;
    
    private String parentEventId;
    
    private String traceId;
    
    private String spanId;
}
Event Types Enum
java
public enum EventType {
    
    // Shipment Events
    SHIPMENT_CREATED("shipment.created"),
    SHIPMENT_ASSIGNED("shipment.assigned"),
    SHIPMENT_PICKED_UP("shipment.picked_up"),
    SHIPMENT_IN_TRANSIT("shipment.in_transit"),
    SHIPMENT_DELIVERED("shipment.delivered"),
    SHIPMENT_FAILED("shipment.failed"),
    SHIPMENT_CANCELLED("shipment.cancelled"),
    SHIPMENT_DELAYED("shipment.delayed"),
    
    // Order Events
    ORDER_PLACED("order.placed"),
    ORDER_CONFIRMED("order.confirmed"),
    ORDER_PROCESSING("order.processing"),
    ORDER_COMPLETED("order.completed"),
    ORDER_CANCELLED("order.cancelled"),
    
    // Payment Events
    PAYMENT_INITIATED("payment.initiated"),
    PAYMENT_SUCCESSFUL("payment.successful"),
    PAYMENT_FAILED("payment.failed"),
    PAYMENT_REFUNDED("payment.refunded"),
    
    // Tracking Events
    LOCATION_UPDATED("location.updated"),
    STATUS_CHANGED("status.changed"),
    ETA_UPDATED("eta.updated"),
    
    // Notification Events
    NOTIFICATION_SENT("notification.sent"),
    NOTIFICATION_DELIVERED("notification.delivered"),
    NOTIFICATION_READ("notification.read"),
    
    // Audit Events
    USER_LOGGED_IN("user.logged_in"),
    USER_LOGGED_OUT("user.logged_out"),
    PERMISSION_CHANGED("permission.changed"),
    SETTINGS_UPDATED("settings.updated");
    
    private final String value;
}
Shipment Event Schema
java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Shipment-related event payload")
public class ShipmentEvent {
    
    @NotBlank
    @Schema(description = "Shipment ID", example = "SHIP-12345")
    private String shipmentId;
    
    @NotBlank
    @Schema(description = "Tracking number", example = "TRK987654321")
    private String trackingNumber;
    
    @Schema(description = "Previous status")
    private ShipmentStatus previousStatus;
    
    @NotNull
    @Schema(description = "Current status")
    private ShipmentStatus currentStatus;
    
    @Schema(description = "Driver ID if assigned")
    private String driverId;
    
    @Schema(description = "Vehicle ID if assigned")
    private String vehicleId;
    
    @Schema(description = "Estimated time of arrival")
    private ZonedDateTime estimatedArrival;
    
    @Schema(description = "Actual arrival time")
    private ZonedDateTime actualArrival;
    
    @Schema(description = "Delivery proof information")
    private DeliveryProof deliveryProof;
    
    @Schema(description = "Reason for status change")
    private String reason;
    
    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;
}
Event Envelope
java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {
    
    @NotBlank
    private String id;
    
    @NotNull
    private EventType type;
    
    @NotNull
    private ZonedDateTime timestamp;
    
    @NotBlank
    private String source;
    
    @NotBlank
    private String specVersion;
    
    @NotNull
    private T data;
    
    @NotNull
    private Map<String, Object> extensions;
    
    public static <T> EventEnvelope<T> create(
        EventType type,
        T data,
        String source
    ) {
        return EventEnvelope.<T>builder()
            .id(UUID.randomUUID().toString())
            .type(type)
            .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
            .source(source)
            .specVersion("1.0")
            .data(data)
            .extensions(new HashMap<>())
            .build();
    }
}
🚀 Usage Examples
Creating an Event
java
public EventEnvelope<ShipmentEvent> createShipmentCreatedEvent(
    Shipment shipment
) {
    ShipmentEvent payload = ShipmentEvent.builder()
        .shipmentId(shipment.getId())
        .trackingNumber(shipment.getTrackingNumber())
        .currentStatus(ShipmentStatus.CREATED)
        .previousStatus(null)
        .estimatedArrival(shipment.getEstimatedDelivery())
        .metadata(Map.of(
            "customerId", shipment.getCustomerId(),
            "serviceType", shipment.getServiceType().name()
        ))
        .build();
    
    return EventEnvelope.create(
        EventType.SHIPMENT_CREATED,
        payload,
        "shipment-service"
    );
}
Consuming Events
java
@Component
@Slf4j
public class ShipmentEventListener {
    
    @KafkaListener(
        topics = "${kafka.topics.shipment-events}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleShipmentEvent(
        @Payload EventEnvelope<ShipmentEvent> envelope
    ) {
        log.info("Received shipment event: {}", envelope.getType());
        
        ShipmentEvent payload = envelope.getData();
        
        switch (envelope.getType()) {
            case SHIPMENT_CREATED:
                handleShipmentCreated(payload);
                break;
            case SHIPMENT_DELIVERED:
                handleShipmentDelivered(payload);
                break;
            case SHIPMENT_FAILED:
                handleShipmentFailed(payload);
                break;
            default:
                log.warn("Unhandled event type: {}", envelope.getType());
        }
    }
    
    private void handleShipmentCreated(ShipmentEvent event) {
        // Update tracking service
        // Send notification to customer
        // Update dashboard
    }
}
Avro Schema Example
json
{
  "type": "record",
  "name": "ShipmentEvent",
  "namespace": "com.logistics.events",
  "fields": [
    {
      "name": "shipmentId",
      "type": "string",
      "doc": "Unique shipment identifier"
    },
    {
      "name": "trackingNumber",
      "type": "string",
      "doc": "Public tracking number"
    },
    {
      "name": "currentStatus",
      "type": {
        "type": "enum",
        "name": "ShipmentStatus",
        "symbols": ["CREATED", "ASSIGNED", "PICKED_UP", "IN_TRANSIT", "DELIVERED", "FAILED"]
      }
    },
    {
      "name": "timestamp",
      "type": "long",
      "logicalType": "timestamp-millis"
    }
  ]
}
📦 Configuration
Event Configuration
yaml
events:
  schema-registry:
    url: ${SCHEMA_REGISTRY_URL:http://localhost:8081}
    compatibility: BACKWARD
  
  kafka:
    topics:
      shipment-events: logistics.shipment.events
      order-events: logistics.order.events
      payment-events: logistics.payment.events
      notification-events: logistics.notification.events
      audit-events: logistics.audit.events
    
    partitions: 3
    replication-factor: 2
    retention-ms: 604800000  # 7 days
  
  rabbitmq:
    exchanges:
      logistics-events:
        name: logistics.events
        type: topic
        durable: true
    
    queues:
      shipment-queue:
        name: logistics.shipment.queue
        routing-key: shipment.*
        durable: true
📦 Maven Dependency
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>event-contracts</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
🔧 Building
bash
# Build library
cd shared-lib/event-contracts
mvn clean install

# Generate Avro classes
mvn generate-sources

# Run tests
mvn test

# Validate schemas
mvn avro:validate
🧪 Testing Strategy
Schema validation tests

Serialization/deserialization tests

Compatibility tests

Contract tests

Example generation

🚀 Performance Considerations
Efficient serialization formats (Avro/Protobuf)

Schema registry caching

Connection pooling for brokers

Batch event processing

Compression for large payloads

🔒 Security Notes
Event validation before processing

Schema validation

Authentication for event producers/consumers

Encryption for sensitive event data

Access control for event topics

📝 Best Practices
Use schema versioning

Maintain backward compatibility

Include correlation IDs

Add timestamps with timezone

Validate events before publishing

Use dead-letter queues for failed events

Monitor event throughput and latency

🔄 Schema Evolution Rules
Add new optional fields

Never remove required fields

Field names are immutable

Use default values for new fields

Update version numbers

Test compatibility

📊 Monitoring
Event publication rate

Event consumption rate

Processing latency

Error rates

Schema compatibility

Consumer lag