# Tenant Onboarding Service

Self-service tenant onboarding with automated provisioning and Stripe subscription management.

## Overview

This service enables automated tenant registration,

 account setup, Stripe subscription integration, and trial period management. It provides a multi-step onboarding wizard that guides new tenants through company information, service configuration, payment setup, and final provisioning.

## Features

- ✅ **Multi-Step Onboarding Wizard** - Guided 4-step onboarding process
- ✅ **Stripe Integration** - Customer, payment method, and subscription management
- ✅ **Trial Period Management** - 14-day trial with auto-tracking
- ✅ **Subscription Plans** - STARTER, GROWTH, ENTERPRISE tiers
- ✅ **Progress Tracking** - Step-by-step completion monitoring
- ✅ **Event Publishing** - Kafka events for onboarding milestones
- ✅ **Email Sequence** - Automated welcome and check-in emails

## Onboarding Wizard

### Step 1: Company Information
- Company details
- Contact information  
- Business type and industry
- Expected order volume

### Step 2: Service Configuration
- Feature selection
- Integration preferences
- Customization options

### Step  3: Payment Setup
- Stripe payment method
- Subscription plan selection
- Trial activation

### Step 4: Completion
- Final provisioning
- Account activation
- Welcome email

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/onboarding/start` | POST | Start onboarding process |
| `/api/onboarding/{id}/subscription` | POST | Setup subscription |
| `/api/onboarding/{id}/complete` | POST | Complete onboarding |
| `/api/onboarding/{id}` | GET | Get onboarding status |

## Usage Examples

### 1. Start Onboarding
```bash
curl -X POST http://localhost:8097/api/onboarding/start \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Acme Logistics",
    "companyEmail": "contact@acme.com",
    "contactPersonName": "John Doe",
    "contactPersonEmail": "john@acme.com",
    "businessType": "B2B",
    "industry": "Manufacturing",
    "expectedMonthlyOrders": 5000,
    "country": "USA",
    "city": "New York"
  }'
```

**Response:**
```json
{
  "id": 1,
  "companyName": "Acme Logistics",
  "status": "IN_PROGRESS",
  "currentStep": 1,
  "totalSteps": 4,
  "companyInfoCompleted": true
}
```

### 2. Setup Subscription
```bash
curl -X POST http://localhost:8097/api/onboarding/1/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "subscriptionPlan": "GROWTH",
    "paymentMethodId": "pm_card_visa",
    "startTrial": true
  }'
```

### 3. Complete Onboarding
```bash
curl -X POST http://localhost:8097/api/onboarding/1/complete
```

## Subscription Plans

| Plan | Features | Price |
|------|----------|-------|
| **STARTER** | Basic features, 1000 orders/month | $99/month |
| **GROWTH** | Advanced features, 10,000 orders/month | $299/month |
| **ENTERPRISE** | All features, unlimited orders | Custom |

## Trial Management

- **Duration:** 14 days (configurable)
- **Auto-Start:** Trial starts immediately on subscription creation
- **Conversion:** Manual or automatic based on configuration
- **Expiry Tracking:** Daily job checks for expiring trials

## Configuration

### application.yml
```yaml
stripe:
  api-key: sk_test_your_key
  webhook-secret: whsec_your_secret

trial:
  duration-days: 14
  auto-convert-to-paid: false

onboarding:
  email:
    welcome-enabled: true
    setup-guide-enabled: true
    check-in-days: 3,7,14
```

## Events

### TenantOnboardingStarted
Published when a new tenant starts onboarding.

### TenantOnboardingCompleted
Published when onboarding is fully completed.

## Database Schema

```sql
CREATE TABLE tenant_onboarding (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT UNIQUE,
    company_name VARCHAR(255) NOT NULL,
    company_email VARCHAR(255) NOT NULL,
    status VARCHAR(50),
    current_step INT,
    
    -- Subscription
    subscription_plan VARCHAR(50),
    stripe_customer_id VARCHAR(255),
    stripe_subscription_id VARCHAR(255),
    stripe_payment_method_id VARCHAR(255),
    
    -- Trial
    is_trial BOOLEAN DEFAULT true,
    trial_start_date TIMESTAMP,
    trial_end_date TIMESTAMP,
    trial_converted BOOLEAN DEFAULT false,
    
    -- Progress
    company_info_completed BOOLEAN,
    service_config_completed BOOLEAN,
    payment_setup_completed BOOLEAN,
    setup_completed BOOLEAN,
    
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Monitoring

- Health: `http://localhost:8097/actuator/health`
- Metrics: `http://localhost:8097/actuator/metrics`

## Future Enhancements

- [ ] Automated tenant provisioning (database, schema)
- [ ] Email sequence automation
- [ ] Guided setup tours
- [ ] Onboarding analytics
- [ ] Multi-currency support
- [ ] Custom branding during onboarding
