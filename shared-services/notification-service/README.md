markdown
# Notification Service

Multi-channel notification delivery system.

## Purpose
- Email notifications (SMTP, SendGrid, AWS SES)
- SMS notifications (Twilio, AWS SNS)
- Push notifications (Firebase, APNS)
- In-app notifications
- WebSocket real-time notifications
- Notification templates and localization

## Channels Supported
- Email (HTML/Template)
- SMS (Text/Localization)
- Mobile Push (iOS/Android)
- Web Push
- Slack/Teams Webhooks
- WhatsApp (Business API)

## API Endpoints
POST /api/v1/notifications/email - Send email
POST /api/v1/notifications/sms - Send SMS
POST /api/v1/notifications/push - Send push notification
POST /api/v1/notifications/in-app - Create in-app notification
GET /api/v1/notifications - List notifications
GET /api/v1/notifications/{id} - Get notification status
POST /api/v1/notifications/template - Create template
PUT /api/v1/notifications/template/{id} - Update template

text

## Template Engine
- Thymeleaf for HTML emails
- Mustache for SMS templates
- JSON configuration for dynamic content
- Multi-language support (en, hi, ta, te, etc.)