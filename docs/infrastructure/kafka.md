# 📨 Kafka & Zookeeper

## Overview
Apache Kafka is the backbone of our event-driven architecture. It handles all asynchronous communication between modules.

## Requirements
- **Kafka Image**: `confluentinc/cp-kafka:7.5.0`
- **Zookeeper Image**: `confluentinc/cp-zookeeper:7.5.0`
- **Memory**: Minimum 1GB allocated via `KAFKA_HEAP_OPTS`.

## Features
- **Saga Orchestration**: Manages complex distributed transactions.
- **Real-time Tracking**: Streams driver location updates to the tracking service.
- **Kafka Streams**: Used for real-time heatmap and metrics computation.

## Connectivity
| Layer | Host | Port | Protocol |
|-------|------|------|----------|
| Internal (Docker) | `kafka` | `29092` | PLAINTEXT |
| External (Host) | `localhost` | `9092` | PLAINTEXT_HOST |

## Key Topics
- `order-status-events`: Lifecycle of an order.
- `driver-locations`: Continuous stream of GPS coordinates.
- `payment-events`: Finance and billing status updates.

## Management
You can use tools like **Offset Explorer** or **Confluent Control Center** (if installed) to inspect topics externally using `localhost:9092`.
