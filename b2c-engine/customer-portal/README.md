markdown
# Customer Portal Service

Customer-facing features and self-service portal.

## Purpose
- Customer registration and profile management
- Order history and tracking
- Support ticket system
- Loyalty program management
- Reviews and ratings

## Features
- Real-time order tracking
- Delivery preferences management
- Address book management
- Invoice download
- Chat support integration
- Service feedback collection

## API Endpoints
GET /api/v1/customers/me - Get customer profile
PUT /api/v1/customers/me - Update profile
GET /api/v1/customers/orders - Order history
GET /api/v1/customers/addresses - Saved addresses
POST /api/v1/customers/addresses - Add address
PUT /api/v1/customers/addresses/{id} - Update address
POST /api/v1/customers/support - Create support ticket
GET /api/v1/customers/support - List tickets
POST /api/v1/customers/feedback - Submit feedback
GET /api/v1/customers/loyalty - Loyalty points
POST /api/v1/customers/notification - Notification preferences

text

## Integration
- Auth Service: Customer authentication
- Order Service: Order details
- Tracking Service: Real-time updates
- Notification Service: Customer communications