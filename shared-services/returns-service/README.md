markdown
# Returns Service

Reverse logistics and returns management for B2C.

## Purpose
- Return request management
- Reverse pickup scheduling
- Refund processing
- Quality inspection
- Returns analytics

## Return Reasons
- Product damaged
- Wrong item delivered
- Size/fit issues
- Changed mind
- Late delivery
- Missing items

## API Endpoints
POST /api/v1/returns - Initiate return
GET /api/v1/returns - List returns
GET /api/v1/returns/{id} - Get return details
PUT /api/v1/returns/{id} - Update return status
POST /api/v1/returns/{id}/pickup - Schedule pickup
POST /api/v1/returns/{id}/inspect - Record inspection
POST /api/v1/returns/{id}/refund - Process refund
GET /api/v1/returns/{id}/timeline - Return timeline
POST /api/v1/returns/{id}/approve - Approve return
POST /api/v1/returns/{id}/reject - Reject return

text

## Return Workflow
REQUESTED → APPROVED → PICKUP_SCHEDULED → PICKED_UP →
INSPECTING → REFUND_PROCESSING → COMPLETED
↓
REJECTED

text

## Integration
- Parcel Service: Original order details
- Billing Service: Refund processing
- Customer Portal: Customer communication