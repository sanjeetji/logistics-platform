# Finance Module API Reference Guide

All endpoints for Payments, Wallets, Pricing, Invoices, Payouts, Loyalty, and Promo Codes.

## Base URL
`http://localhost:8080`

---

## 1. Payment Controller (`/api/v1/payments`)
*Payment processing, wallet top-ups, and reconciliation.*

### Create Payment Wallet
* **Endpoint**: `POST /api/v1/payments/wallet?userId=1`
* **Response**: `200 OK` with `PaymentWallet`.

### Get Payment Wallet
* **Endpoint**: `GET /api/v1/payments/wallet/{userId}`
* **Response**: `200 OK` with `PaymentWallet` (balance, userId, etc.).

### Top Up Wallet
* **Endpoint**: `POST /api/v1/payments/topup`
* **Request Body**: `{"userId": 1, "amount": 500.00, "gatewayType": "STRIPE"}`
* **Response**: `200 OK` with updated `PaymentWallet`.

### Initiate Top-Up (Payment Gateway)
* **Endpoint**: `POST /api/v1/payments/topup/initiate`
* **Request Body**: Same as topup.
* **Response**: `200 OK` with `{paymentUrl, sessionId}` for redirect.

### Process Payment
* **Endpoint**: `POST /api/v1/payments/process`
* **Request Body**: `{"orderId": "ORD-001", "userId": 1, "amount": 150.00}`
* **Response**: `200 OK`.

### Get Payment History
* **Endpoint**: `GET /api/v1/payments/history/{userId}`
* **Response**: List of `Transaction` objects.

### Reconcile Payments
* **Endpoint**: `POST /api/v1/payments/reconcile?gatewayType=STRIPE&from=2024-01-01T00:00:00&to=2024-01-31T23:59:59`
* **Response**: `200 OK` with `ReconciliationRecord`.

### Process Payout
* **Endpoint**: `POST /api/v1/payments/payout`
* **Request Body**: `{"driverId": 1, "amount": 2000.00, "method": "BANK_TRANSFER"}`
* **Response**: `200 OK`.

---

## 2. Wallet Controller (`/api/v1/wallets`)
*In-app wallet for customers and drivers.*

### Create Wallet
* **Endpoint**: `POST /api/v1/wallets?userId=user-uuid`
* **Response**: `200 OK` with `Wallet`.

### Get Wallet
* **Endpoint**: `GET /api/v1/wallets/{userId}`
* **Response**: `200 OK` with balance and transaction summary.

### Top Up Wallet
* **Endpoint**: `POST /api/v1/wallets/{userId}/topup`
* **Request Body**: `{"amount": 1000.00, "referenceId": "TXN-123"}`
* **Response**: `200 OK` with updated `Wallet`.

### Deduct from Wallet
* **Endpoint**: `POST /api/v1/wallets/{userId}/deduct`
* **Request Body**: `{"amount": 150.00, "referenceId": "ORD-001", "description": "Order payment"}`
* **Response**: `200 OK` with updated `Wallet`.

---

## 3. Pricing Controller (`/api/v1/pricing`)
*Price estimation for delivery orders.*

### Calculate Price Estimate
* **Endpoint**: `POST /api/v1/pricing/estimate`
* **Request Body**:
    ```json
    {
      "pickupLat": 28.6139,
      "pickupLng": 77.2090,
      "deliveryLat": 28.7041,
      "deliveryLng": 77.1025,
      "weightKg": 2.5,
      "serviceLevel": "STANDARD"
    }
    ```
* **Field Comments**:
    * `serviceLevel`: `STANDARD`, `EXPRESS`, `SAME_DAY`, `SCHEDULED`
* **Response**: List of `PriceEstimateResponse` (one per service level).

### Get Price Estimate by ID
* **Endpoint**: `GET /api/v1/pricing/estimate/{estimateId}`
* **Response**: `200 OK` with `PriceEstimate`.

### Get Price Estimate by Order
* **Endpoint**: `GET /api/v1/pricing/estimate/order/{orderId}`
* **Response**: `200 OK` with `PriceEstimate` linked to order.

---

## 4. Dynamic Pricing Controller (`/api/v1/pricing/dynamic`)
*Surge pricing and demand-based pricing.*

### Get Surge Multiplier
* **Endpoint**: `GET /api/v1/pricing/dynamic/surge?zone=NORTH_DELHI`
* **Response**: `{"multiplier": 1.8, "zone": "NORTH_DELHI"}`.

### Update Surge Settings
* **Endpoint**: `PUT /api/v1/pricing/dynamic/surge`
* **Auth**: `ADMIN` only.
* **Request Body**: `{"zone": "NORTH_DELHI", "multiplier": 2.0, "reason": "Peak hours"}`
* **Response**: `200 OK`.

---

## 5. Invoice Controller (`/api/v1/invoices`)
*Invoice generation and PDF export.*

### Generate Invoice
* **Endpoint**: `POST /api/v1/invoices`
* **Request Body**:
    ```json
    {
      "clientId": "CLIENT-001",
      "orderId": "ORD-001",
      "amount": 500.00,
      "dueDate": "2024-02-01"
    }
    ```
* **Response**: `200 OK` with `Invoice` object including `invoiceNumber`.

### Download Invoice PDF
* **Endpoint**: `GET /api/v1/invoices/{id}/pdf`
* **Response**: `application/pdf` binary — downloads as `invoice_<number>.pdf`.

### Get Pending Invoices for Client
* **Endpoint**: `GET /api/v1/invoices/pending/{clientId}`
* **Response**: List of unpaid `Invoice` objects.

---

## 6. Payout Controller (`/api/v1/payouts`)
*Driver earnings and payout management.*

### Get Driver Earnings
* **Endpoint**: `GET /api/v1/payouts/driver/{driverId}`
* **Response**: Earnings breakdown (daily, weekly, pending).

### Trigger Payout
* **Endpoint**: `POST /api/v1/payouts/trigger`
* **Request Body**: `{"driverId": 1, "period": "WEEKLY"}`
* **Response**: `200 OK` with payout record.

---

## 7. Promo Code Controller (`/api/v1/promo-codes`)
*Promotional code management.*

### Create Promo Code
* **Endpoint**: `POST /api/v1/promo-codes`
* **Auth**: `ADMIN` only.
* **Request Body**:
    ```json
    {
      "code": "SAVE20",
      "discountPercent": 20,
      "maxUses": 100,
      "expiresAt": "2024-12-31T23:59:59"
    }
    ```
* **Response**: `201 Created`.

### Apply Promo Code
* **Endpoint**: `POST /api/v1/promo-codes/apply`
* **Request Body**: `{"code": "SAVE20", "orderId": "ORD-001", "userId": 1}`
* **Response**: `200 OK` with discount applied amount.

### Validate Promo Code
* **Endpoint**: `GET /api/v1/promo-codes/validate?code=SAVE20`
* **Response**: `{"valid": true, "discountPercent": 20}`.

---

## 8. Loyalty Controller (`/api/v1/loyalty`)
*Customer loyalty points program.*

### Get Loyalty Points
* **Endpoint**: `GET /api/v1/loyalty/{userId}`
* **Response**: `{"points": 500, "tier": "GOLD"}`.

### Redeem Points
* **Endpoint**: `POST /api/v1/loyalty/{userId}/redeem`
* **Request Body**: `{"points": 100, "orderId": "ORD-001"}`
* **Response**: `200 OK` with updated balance.
