# Logistics Platform: End-to-End Workflow Guide

This document describes the complete flow of the Logistics Platform, covering both B2B (Business-to-Business) and B2C (Consumer) operations.

---

## 1. High-Level Architecture
The platform is divided into two main engines:
1.  **B2B Engine**: Handles large-scale logistics, warehouse management, and corporate shipments.
2.  **B2C Engine**: Handles consumer parcel delivery, last-mile logistics, and mobile app interactions.

**Core Services (Shared)**: Authentication, User Management, Payment, Notification.

---

## 2. B2B Workflow (Corporate Logistics)

### Scenario: A Manufacturer ships goods to a Retailer

1.  **Order Placement**
    *   **Actor**: Business Client (Manufacturer)
    *   **Action**: Places a bulk order via Web Portal.
    *   **Service**: `b2b-order-service`
    *   **Data**: Items: 500 units of Electronics, Source: Warehouse A, Destination: Retail Store B.

2.  **Inventory Check**
    *   **System**: Automatically checks stock levels.
    *   **Service**: `inventory-service` -> `warehouse-service`
    *   **Outcome**: Stock reserved.

3.  **Shipment Creation**
    *   **System**: Groups items into a Shipment.
    *   **Service**: `shipment-service`
    *   **Action**: Generates a Bill of Lading (BOL) and assigns a Truck.

4.  **Compliance Check**
    *   **System**: Verifies regulatory requirements (e.g., Hazardous Materials).
    *   **Service**: `compliance-service`

5.  **Dispatch & Tracking**
    *   **Actor**: Truck Driver
    *   **Service**: `fleet-service` / `tracking-service`
    *   **Action**: Driver starts trip. GPS location is streamed in real-time.

6.  **Delivery & Billing**
    *   **Actor**: Receiver (Retailer) signs digitally.
    *   **Service**: `billing-service` -> `payment-service`
    *   **Outcome**: Invoice generated and auto-debited.

---

## 3. B2C Workflow (Last-Mile Delivery)

### Scenario: A Customer orders a Pizza (On-Demand) or a Parcel (E-commerce)

1.  **Order Request**
    *   **Actor**: Customer (via Mobile App)
    *   **Service**: `customer-api-service` -> `order-service`
    *   **Data**: "Deliver to 123 Main St."

2.  **Driver Matching (The "Uber" Logic)**
    *   **System**: Finds nearby available drivers.
    *   **Service**: `dispatch-service` + `geo-service`
    *   **Logic**: Matches based on proximity and vehicle type (Bike/Scooter).

3.  **Assignment & Pickup**
    *   **Actor**: Courier/Driver (via Driver App)
    *   **Service**: `driver-api-service`
    *   **Action**: Driver accepts order, navigates to pickup point.

4.  **Live Tracking**
    *   **Actor**: Customer tracks driver on map.
    *   **Service**: `tracking-service` (WebSocket/Kafka)
    *   **Update**: "Driver is 2 mins away".

5.  **Proof of Delivery**
    *   **Actor**: Driver takes photo or gets OTP.
    *   **Service**: `parcel-service`
    *   **Outcome**: Order marked "DELIVERED".

6.  **Notification & Rating**
    *   **System**: Sends SMS/Push Notification.
    *   **Actor**: Customer rates the driver (1-5 stars).
    *   **Service**: `notification-service`, `rating-service`.

---

## 4. Key Integration Points

*   **Kafka**: Used for all asynchronous events (e.g., "Order Created" -> triggers "Inventory Check").
*   **Redis**: Used for caching real-time driver locations and user sessions.
*   **Postgres**: Stores all persistent transactional data.
