#!/bin/bash

BASE_URL="http://localhost:80"
# Gateway port is 80 on host
# We added RewritePath filters, so we can use /auth-service/api/v1/... and it will rewrite to /api/v1/...
AUTH_URL="$BASE_URL/auth-service/api/v1/auth"
ORDER_URL="$BASE_URL/order-service/api/v1/orders"
DISPATCH_URL="$BASE_URL/dispatch-service/api/v1/dispatch"

# 1. Register
echo "1. Registering User..."
curl -X POST "$AUTH_URL/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@logistics.com",
    "password": "password123",
    "phone": "555-123-4567",
    "userType": "CUSTOMER"
  }'
echo -e "\n"

# 2. Login
echo "2. Logging In..."
LOGIN_RESPONSE=$(curl -s -X POST "$AUTH_URL/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test.user@logistics.com",
    "password": "password123"
  }')
echo "Login Response: $LOGIN_RESPONSE"

# Extract Token (Simple grep/sed extraction, assuming JSON structure)
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"data":"[^"]*' | cut -d'"' -f4)
echo "Token: $TOKEN"

if [ -z "$TOKEN" ]; then
  echo "Failed to get token. Exiting."
  exit 1
fi

# 3. Create Order
echo "3. Creating Order..."
ORDER_RESPONSE=$(curl -s -X POST "$ORDER_URL" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust-1",
    "tenantId": "tenant-1",
    "type": "B2C_ON_DEMAND",
    "status": "CREATED",
    "pickupLocation": {
      "address": "123 Pickup St",
      "latitude": 40.7128,
      "longitude": -74.0060,
      "contactName": "Alice",
      "contactPhone": "111-222-3333",
      "instructions": "Ring bell"
    },
    "dropLocation": {
      "address": "456 Drop Ave",
      "latitude": 40.7300,
      "longitude": -74.0100,
      "contactName": "Bob",
      "contactPhone": "444-555-6666",
      "instructions": "Leave at door"
    },
    "weightKg": 10.0,
    "price": 50.0
  }')
echo "Order Response: $ORDER_RESPONSE"

# Extract Order ID (assuming "data": { "id": 1, ... })
# This might fail if the ID is not in top level or structure differs.
# For manual verification, seeing the output is enough.

# 4. Dispatch Order
echo "4. Initiating Dispatch..."
curl -X POST "$DISPATCH_URL/initiate" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-TEST-1",
    "customerId": "cust-1",
    "tenantId": "tenant-1",
    "orderType": "B2C_ON_DEMAND",
    "status": "CREATED",
    "pickupAddress": "123 Pickup St",
    "pickupLat": 40.7128,
    "pickupLng": -74.0060,
    "dropAddress": "456 Drop Ave",
    "dropLat": 40.7300,
    "dropLng": -74.0100,
    "weightKg": 10.0,
    "price": 50.0
  }'
echo -e "\n"
