import json
import os
import copy

collection_path = '/Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform/postman/Logistics-Platform-API.postman_collection.json'

with open(collection_path, 'r') as f:
    collection = json.load(f)

def create_item(name, method, url_path, auth="none", request_body=None, response_body=None, headers=None):
    item = {
        "name": name,
        "request": {
            "method": method,
            "header": headers or [],
            "url": {
                "raw": "{{baseUrl}}" + url_path,
                "host": ["{{baseUrl}}"],
                "path": [p for p in url_path.split("/") if p]
            }
        },
        "response": []
    }
    
    if auth != "none":
        item["request"]["auth"] = {
            "type": "bearer",
            "bearer": [
                {
                    "key": "token",
                    "value": "{{authToken}}",
                    "type": "string"
                }
            ]
        }
    else:
        item["request"]["auth"] = {
            "type": "noauth"
        }

    if request_body:
        item["request"]["body"] = {
            "mode": "raw",
            "raw": json.dumps(request_body, indent=4),
            "options": {
                "raw": {
                    "language": "json"
                }
            }
        }
    
    if response_body:
        item["response"].append({
            "name": name + " - Success",
            "originalRequest": item["request"],
            "status": "OK",
            "code": 200,
            "_postman_previewlanguage": "json",
            "header": [
                {"key": "Content-Type", "value": "application/json"}
            ],
            "cookie": [],
            "body": json.dumps(response_body, indent=4)
        })

    return item

# Auth folder
auth_folder = next((item for item in collection.get("item", []) if item.get("name") == "Authentication"), None)

if auth_folder:
    # Check if send OTP exists
    if not any(i.get("name") == "Send OTP" for i in auth_folder.get("item", [])):
        auth_folder["item"].append(create_item(
            name="Send OTP",
            method="POST",
            url_path="/api/auth/send-otp",
            auth="none",
            request_body={"phone": "+1234567890"},
            response_body={
                "success": True,
                "message": "OTP sent successfully. Static OTP is active.",
                "verificationStatus": "PENDING",
                "phone": "+1234567890"
            }
        ))
    if not any(i.get("name") == "Verify Phone OTP" for i in auth_folder.get("item", [])):
        auth_folder["item"].append(create_item(
            name="Verify Phone OTP",
            method="POST",
            url_path="/api/auth/verify-phone",
            auth="none",
            request_body={"phone": "+1234567890", "otpCode": "123456"},
            response_body={
                "success": True,
                "message": "Phone number verified successfully",
                "verificationStatus": "APPROVED",
                "phone": "+1234567890"
            }
        ))

# Fleet module - find or create Fleet Management folder
fleet_folder = next((item for item in collection.get("item", []) if item.get("name") == "Fleet Management"), None)
if not fleet_folder:
    fleet_folder = {"name": "Fleet Management", "item": []}
    collection["item"].append(fleet_folder)

# Driver Behavior sub-folder
behavior_folder = next((item for item in fleet_folder.get("item", []) if item.get("name") == "Driver Behavior"), None)
if not behavior_folder:
    behavior_folder = {"name": "Driver Behavior", "item": []}
    fleet_folder["item"].append(behavior_folder)

if not any(i.get("name") == "Report Behavior Event" for i in behavior_folder.get("item", [])):
    behavior_folder["item"].append(create_item(
        name="Report Behavior Event",
        method="POST",
        url_path="/api/v1/fleet/behavior/events",
        auth="bearer",
        headers=[{"key": "X-Tenant-ID", "value": "{{tenantId}}", "type": "text"}],
        request_body={
            "driverExternalId": "drv-123",
            "eventType": "SPEEDING",
            "timestamp": "2026-03-01T10:00:00Z",
            "severity": 1.2,
            "latitude": 37.7749,
            "longitude": -122.4194
        },
        response_body={
            "success": True,
            "message": "Behavior event recorded and processed.",
            "data": None,
            "timestamp": "2026-03-01T10:05:00Z"
        }
    ))

if not any(i.get("name") == "Get Driver Analytics" for i in behavior_folder.get("item", [])):
    behavior_folder["item"].append(create_item(
        name="Get Driver Analytics",
        method="GET",
        url_path="/api/v1/fleet/behavior/analytics/drv-123",
        auth="bearer",
        headers=[{"key": "X-Tenant-ID", "value": "{{tenantId}}", "type": "text"}],
        response_body={
            "success": True,
            "message": "Advanced analytics for driver drv-123",
            "data": None,
            "timestamp": "2026-03-01T10:05:00Z"
        }
    ))

with open(collection_path, 'w') as f:
    json.dump(collection, f, indent=4)

print("Updated Postman collection successfully.")
