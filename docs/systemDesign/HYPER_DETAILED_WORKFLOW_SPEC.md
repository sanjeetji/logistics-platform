# Logistics Platform: Hyper-Detailed Architecture & Workflow Spec

This document provides a low-level "source of truth" for the Logistics Platform's internal mechanics, covering database DDLs, SAGA state transitions, and granular resilience configurations.

---

## 1. Database & Persistence "Source of Truth"

### A. SAGA Orchestration Schema (`saga_states`)
The `orchestration-module` persists the state of every order lifecycle to ensure recovery after service restarts.

```sql
CREATE TABLE saga_states (
    order_id VARCHAR(255) PRIMARY KEY,
    status VARCHAR(50) NOT NULL, -- STARTED, INVENTORY_RESERVED, PAYMENT_PROCESSED, etc.
    current_step VARCHAR(100),   -- e.g., 'WAITING_FOR_DRIVER'
    failure_reason TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_saga_status ON saga_states(status);
```

### B. Transactional Outbox Schema (`outbox`)
Used to ensure atomicity between DB state changes and Kafka event publishing.

```sql
CREATE TABLE outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL, -- 'ORDER', 'TENANT', 'DRIVER'
    aggregate_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,           -- 'ORDER_CREATED', 'DRIVER_STATUS_CHANGED'
    payload JSONB NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_outbox_unprocessed ON outbox(created_at) WHERE processed_at IS NULL;
```

---

## 2. Low-Level SAGA State Machine Transitions

The platform utilizes a **Choreographed-Orchestration** hybrid. Below is the granular code-path for the `OrderCreationSaga`.

| Trigger Event | Current State | Logic Executed | New State | Next Command |
| :--- | :--- | :--- | :--- | :--- |
| `OrderCreatedEvent` | `NULL` | Initialize Saga Record | `STARTED` | `ALLOCATE_INVENTORY` |
| `InventoryReserved` | `STARTED` | Log Step Completion | `INVENTORY_RESERVED` | `PROCESS_PAYMENT` |
| `PaymentProcessed` | `INVENTORY_RESERVED` | Authorize Funds via Stripe | `PAYMENT_PROCESSED` | `CONFIRM_ORDER` |
| `OrderConfirmed` | `PAYMENT_PROCESSED` | Publish to `order.confirmed` | `ORDER_CONFIRMED` | `DISPATCH_ORDER` |

### Failure & Compensation Logic
If `PAYMENT_PROCESSED` fails (Stripe error):
1.  **Detector**: `OrderCreationSaga.failSaga()` catches `PaymentFailedEvent`.
2.  **Action**: Updates state to `FAILED`.
3.  **Compensation**: Emits `CancelOrderCommand` to release blocked inventory and notify the user via `order.payment.failed`.

---

## 3. Dispatch Scoring Algorithm (Distance + Capacity)

The `DispatchScoringEngine` utilizes a weighted-rule system to find the optimal driver.

### A. Constraint Check (Soft/Hard Barriers)
Before scoring, candidates are filtered via `DispatchConstraint` implementations:
*   **VehicleCapacityConstraint**: `if (vehicleCapacity > 0) return vehicleCapacity >= orderWeight;`

### B. Scoring Logic (`DistanceScoringRule`)
The primary scoring factor is the **Haversine Distance**:
```java
// Logic inside DistanceScoringRule.java
double score = Math.max(0, 100 - (distanceInKm * 2));
int weight = 10;
// Result: A driver 5km away gets (100 - 10) * 10 = 900 base points.
```

---

## 4. Real-Time Streaming & Analytics Topologies

The platform uses Spring Cloud Stream with Kafka Streams for real-time intelligence.

### A. Driver Location & Tracking Stream
*   **Processor**: `processDriverLocations` in `TrackingStreamConfig.java`
*   **Topology**: Ingests `DriverLocationDto` -> Filters invalid coordinates -> Maps to `TrackingEvent` with enriched metadata.

### B. SLA Analytics & Violation Pipeline
*   **Processor**: `processSLAMetrics` in `SLAMetricsProcessor.java`
*   **Functionality**: 
    1.  Subscribes to `SLABreachPredictedEvent`.
    2.  Aggregates total breaches per `Region` or `Tenant` using a **Tumbling Window** (e.g., 5-minute interval).
    3.  Pipes real-time aggregations to the `operational-dashboard-topic` for Control Tower.

### C. Live Operational Heatmap
*   **Processor**: `processHeatmap` in `HeatmapStreamConfig.java`
*   **Logic**: 
    1.  Consumes raw tracking pings.
    2.  Groups by **Geohash** (Precision Level 5-6).
    3.  Produces a `HeatmapData` stream used by the B2C customer app and ops dashboard.

---

## 5. Resilience & Operational Tuning

### Resilience4j Configurations
Platform support uses strict retry and circuit breaker policies to prevent cascading module failure.

```yaml
# Source: platform-support-module/application.yml
resilience4j:
  retry:
    instances:
      webhookRetry:
        max-attempts: 5
        wait-duration: 2s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
```

### Manual Override Protocols
In the **Control Tower Service**, an Ops Manager can issue a `ManualReassignmentCommand`.
*   **Protocol**: This command overrides the `SagaState` status to `DISPATCH_REQUESTED` and manually injects the `driver_id` into the `DispatchAssignment` record, bypassing the recommendation engine.

---

## 5. Operational Resilience & Failure Recovery

The platform implements multi-layer recovery to handle transient and permanent infrastructure failures.

### A. Kafka Partition & Consumer Failure
*   **Detector**: `ConsumerRebalanceListener` logs and health-check endpoints (`/actuator/health`).
*   **Recovery Protocol**:
    1.  **Consumer Lag Spike**: HPA (Horizontal Pod Autoscaler) triggers scaling of consumer pods up to the partition count.
    2.  **Poison Pill (Deserialization Error)**: Caught by `ErrorHandlingDeserializer`. Event is piped to a **DLQ (Dead Letter Queue)** for manual inspection.
    3.  **Partition Downtime**: The **Transactional Outbox** prevents data loss; events remain in the `outbox` table until Kafka returns a `SUCCESS` ack.

### B. Manual Override Protocols (Control Tower)
When an exception cannot be auto-resolved (e.g., all re-optimizations fail), the **Control Tower** provides intervention hooks:

1.  **Direct Assignment Override**:
    *   **Action**: `POST /api/control-tower/override/assignment`
    *   **Mechanism**: Issues a `ManualAssignmentCommand` which forces the `SagaState` into `DRIVER_ASSIGNED`, bypassing all scoring and constraint rules.
2.  **SAGA Step Reset**:
    *   **Action**: Allows an admin to "Rewind" a SAGA to a previous state (e.g., back to `PAYMENT_PROCESSED`) to retry fulfillment after an address fix.

---

## 6. Intelligence & ML Service Interface

The `MLService` interaction uses a strict Avro-serialized payload over Kafka for background retraining and a REST endpoint for real-time predictions.

### A. Real-time Prediction (REST)
*   **Endpoint**: `POST /api/v1/ml/predict-eta`
*   **Input**: `RoutePayload` (Source, Destination, VehicleType).
*   **Output**: `PredictionResponse` (Seconds, ConfidenceScore).

### B. Feedback Loop (Streaming)
*   **Topic**: `intelligence.feedback.v1`
*   **Mechanism**: The `MLFeedbackService` collects `ActualArrival` events and Joins them with `PredictionLogs` to compute drift metrics, ensuring the "Brain" of the logistics system continuously learns from execution.
