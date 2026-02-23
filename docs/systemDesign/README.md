# Logistics OS: System Design & Workflow Registry

Welcome to the central documentation vault for the All-In-One Logistics Operating System. This directory contains detailed specifications, architectural diagrams, and operational workflows for the entire platform.

## 🏛️ Core Architecture
*   [**SYSTEM_DESIGN_MASTER.md**](./SYSTEM_DESIGN_MASTER.md): High-level system architecture, orchestration patterns, and data consistency models.
*   [**HYPER_DETAILED_WORKFLOW_SPEC.md**](./HYPER_DETAILED_WORKFLOW_SPEC.md): Low-level state machine transitions, DB DDLs, and Kafka Stream topologies.
*   [**SUPER_ADMIN_WORKFLOW.md**](./SUPER_ADMIN_WORKFLOW.md): Detailed guide on platform owner registration, global tenant management, and oversight.
*   [**MODULE_SUMMARY.md**](./MODULE_SUMMARY.md): Executive report on all 40+ domain modules and their production status.
*   [**API_REFERENCE.md**](./API_REFERENCE.md): Consolidated guide to the platform's REST endpoints (Postman-style).

## 🔄 Domain Workflows
Each document below details a domain's feature set, success sequence, error handling logic, and recovery (retry/fallback) strategies.

*   [**IDENTITY_WORKFLOW.md**](./IDENTITY_WORKFLOW.md): Auth, RBAC, and Multi-tenancy onboarding.
*   [**ORDER_FULFILLMENT_WORKFLOW.md**](./ORDER_FULFILLMENT_WORKFLOW.md): The "Order-to-Delivery" lifecycle and auto-dispatch matching.
*   [**FLEET_MANAGEMENT_WORKFLOW.md**](./FLEET_MANAGEMENT_WORKFLOW.md): Driver lifecycle, shift management, and geospatial compliance.
*   [**B2B_INVENTORY_WORKFLOW.md**](./B2B_INVENTORY_WORKFLOW.md): Warehouse management and enterprise ERP synchronization.
*   [**FINANCE_BILLING_WORKFLOW.md**](./FINANCE_BILLING_WORKFLOW.md): Dynamic pricing engine and automated financial clearing.

## 🛡️ Operational Safety
The platform is designed with a **Resilience-First** mindset. All workflows include specific fallback paths and circuit-breaker strategies to ensure 99.9% uptime for critical logistics operations.
