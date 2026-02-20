#!/bin/bash

# ==============================================================================
# Integration Test: Order -> Dispatch -> Fleet
# ==============================================================================

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

BASE_URL="http://localhost:8080" # Logistic Platform Port

# Logistic Platform Routing: All services are on the same port/host
ORDER_SERVICE_URL="$BASE_URL"
DISPATCH_SERVICE_URL="$BASE_URL"
FLEET_SERVICE_URL="$BASE_URL"
AUTH_SERVICE_URL="$BASE_URL"

# Ensure dependencies (curl, jq)
if ! command -v jq &> /dev/null; then
    echo -e "${RED}Error: jq is required.${NC}"
    exit 1
fi

echo -e "${BLUE}[INFO] Starting Integration Test...${NC}"

# 1. Health Checks (Public endpoints check)
echo -e "${BLUE}[1/5] Checking Service Health...${NC}"
check_health() {
    url=$1
    name=$2
    # Gateway might take time to register routes
    MAX_RETRIES=30
    COUNT=0
    
    while [ $COUNT -lt $MAX_RETRIES ]; do
        # Actuator might be protected, so check just root or wait for 401/200
        code=$(curl -s -o /dev/null -w "%{http_code}" "$url/actuator/health")
        if [ "$code" == "200" ] || [ "$code" == "401" ]; then
            echo -e "${GREEN}[OK] $name is REACHABLE (Status: $code)${NC}"
            return 0
        fi
        sleep 3
        COUNT=$((COUNT+1))
    done
    
    echo -e "${RED}[FAIL] $name is DOWN (Status: $code) - URL: $url/actuator/health${NC}"
    # exit 1 
}

check_health "$AUTH_SERVICE_URL" "Auth Service"
check_health "$ORDER_SERVICE_URL" "Order Service"
check_health "$DISPATCH_SERVICE_URL" "Dispatch Service"
check_health "$FLEET_SERVICE_URL" "Fleet Service"

# 2. Authenticate (Register/Login)
echo -e "${BLUE}[2/5] Authenticating...${NC}"
AUTH_USER='{
  "firstName": "Integration",
  "lastName": "Test",
  "email": "integration.test@logistics.com",
  "password": "password123",
  "phone": "+1000000000",
  "userType": "SUPER_ADMIN"
}'

# Register (Ignore error if exists)
curl -s -X POST "$AUTH_SERVICE_URL/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d "$AUTH_USER" > /dev/null

# Login
LOGIN_RESPONSE=$(curl -s -X POST "$AUTH_SERVICE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{ "email": "integration.test@logistics.com", "password": "password123" }')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data // empty')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
    echo -e "${RED}[FAIL] Login Failed. Response: $LOGIN_RESPONSE${NC}"
    exit 1
fi

echo -e "${GREEN}[OK] Authenticated. Token acquired.${NC}"
AUTH_HEADER="Authorization: Bearer $TOKEN"

# 3. Seed Driver
echo -e "${BLUE}[3/5] Seeding Driver in Fleet Service...${NC}"
DRIVER_JSON='{
  "name": "Integration Test Driver",
  "licenseNumber": "INT-TEST-001",
  "phoneNumber": "+1234567890",
  "email": "test.driver@logistics.com",
  "status": "ONLINE",
  "verificationStatus": "VERIFIED"
}'

# Create Driver
CREATE_DRIVER_RESPONSE=$(curl -s -X POST "$FLEET_SERVICE_URL/api/v1/drivers" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d "$DRIVER_JSON")

# Extract ID 
DRIVER_ID=$(echo $CREATE_DRIVER_RESPONSE | jq -r '.data.id // empty')

if [ -z "$DRIVER_ID" ] || [ "$DRIVER_ID" == "null" ]; then
    echo -e "${BLUE}    Driver might already exist. Fetching available drivers...${NC}"
    AVAILABLE_DRIVERS=$(curl -s -X GET "$FLEET_SERVICE_URL/api/v1/drivers/available" -H "$AUTH_HEADER")
    DRIVER_ID=$(echo $AVAILABLE_DRIVERS | jq -r '.data[0].id // empty')
    
    if [ -z "$DRIVER_ID" ] || [ "$DRIVER_ID" == "null" ]; then
         echo -e "${RED}[FAIL] Could not create or find an available driver.${NC}"
         exit 1
    fi
    echo -e "${GREEN}[OK] Using existing available Driver ID: $DRIVER_ID${NC}"
else
    echo -e "${GREEN}[OK] Created Driver ID: $DRIVER_ID${NC}"
fi

# 4. Create B2B Order
echo -e "${BLUE}[4/5] Creating B2B Order in Order Service...${NC}"
ORDER_JSON='{
  "customerId": "CUST-INT-001",
  "type": "B2B",
  "pickupLocation": {
    "latitude": 40.7128,
    "longitude": -74.0060,
    "address": "New York, NY"
  },
  "dropoffLocation": {
    "latitude": 34.0522,
    "longitude": -118.2437,
    "address": "Los Angeles, CA"
  }
}'

CREATE_ORDER_RESPONSE=$(curl -s -X POST "$ORDER_SERVICE_URL/api/v1/orders" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d "$ORDER_JSON")

ORDER_ID=$(echo $CREATE_ORDER_RESPONSE | jq -r '.data.orderId // empty')

if [ -z "$ORDER_ID" ] || [ "$ORDER_ID" == "null" ]; then
    echo -e "${RED}[FAIL] Failed to create order. Response: $CREATE_ORDER_RESPONSE${NC}"
    exit 1
fi

echo -e "${GREEN}[OK] Created Order ID: $ORDER_ID${NC}"

# 5. Verify Dispatch Assignment
echo -e "${BLUE}[5/5] Verifying Dispatch Assignment (Polling)...${NC}"
MAX_RETRIES=20
RETRY_COUNT=0
MATCHED=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    sleep 3
    echo -n "."
    
    DISPATCH_STATUS_RESPONSE=$(curl -s -X GET "$DISPATCH_SERVICE_URL/api/v1/dispatch/status/$ORDER_ID" -H "$AUTH_HEADER")
    STATUS=$(echo $DISPATCH_STATUS_RESPONSE | jq -r '.data.status // empty')
    MATCHED_DRIVER=$(echo $DISPATCH_STATUS_RESPONSE | jq -r '.data.matchedDriverId // empty')
    
    if [ "$STATUS" == "ASSIGNED" ]; then
        echo ""
        echo -e "${GREEN}[SUCCESS] Order $ORDER_ID ASSIGNED to Driver $MATCHED_DRIVER${NC}"
        MATCHED=true
        break
    elif [ "$STATUS" == "CREATED" ] || [ "$STATUS" == "PENDING" ]; then
        : # Continue waiting
    else
         # If it's something else or null/failed
         :
    fi
    
    RETRY_COUNT=$((RETRY_COUNT+1))
done

if [ "$MATCHED" = false ]; then
    echo ""
    echo -e "${RED}[FAIL] Dispatch job was not assigned in time. Final Status: $STATUS${NC}"
    echo "Response: $DISPATCH_STATUS_RESPONSE"
    exit 1
fi

echo -e "${GREEN}Integration Test Completed Successfully!${NC}"
