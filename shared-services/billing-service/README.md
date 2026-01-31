markdown
# Billing Service

Subscription management, invoicing, and payment processing.

## Purpose
- Subscription plan management
- Recurring billing (monthly/annual)
- Invoice generation and delivery
- Payment gateway integration (Stripe/Razorpay)
- Usage-based billing (per delivery/tracking)
- Refund processing

## Features
- Multiple pricing tiers
- Usage metering and tracking
- Automated invoicing
- Payment failure handling
- Tax calculation (GST/VAT)
- Coupon and discount codes
- Revenue reporting

## API Endpoints
GET /api/v1/subscriptions - List subscriptions
POST /api/v1/subscriptions - Create subscription
GET /api/v1/subscriptions/{id} - Get subscription
PUT /api/v1/subscriptions/{id} - Update subscription
DELETE /api/v1/subscriptions/{id} - Cancel subscription

GET /api/v1/invoices - List invoices
GET /api/v1/invoices/{id} - Get invoice
POST /api/v1/invoices/{id}/pay - Process payment
POST /api/v1/invoices/{id}/refund - Initiate refund

GET /api/v1/usage/{tenantId} - Get usage metrics
POST /api/v1/usage/{tenantId} - Record usage

text

## Payment Gateway Integration
- Stripe (primary)
- Razorpay (India)
- PayPal (backup)
- Manual bank transfer

## Webhooks
- Payment succeeded
- Payment failed
- Subscription renewed
- Invoice paid
- Refund processed