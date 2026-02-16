# Notification Service

Automated email notification service for the Logistics Platform. Handles onboarding emails, trial reminders, and other transactional emails.

## Features

- ✉️ **Email Automation**: Send HTML emails using Thymeleaf templates
- 📧 **Onboarding Emails**: Welcome emails and setup guides
- ⏰ **Trial Reminders**: Automated reminders 7, 3, and 1 days before trial expiry
- 🔔 **Event-Driven**: Kafka integration for real-time notifications
- 📅 **Scheduled Jobs**: Daily scheduler for trial reminders (9 AM)
- 🎨 **HTML Templates**: Beautiful, responsive email templates

## Quick Start

### Option 1: Interactive Setup (Recommended)

Run the quick-start script:

```bash
cd notification-service
./smtp-quickstart.sh
```

This will guide you through:
1. Selecting your email provider (Gmail, SendGrid, AWS SES, etc.)
2. Entering your SMTP credentials
3. Configuring Kafka and database
4. Optionally saving to `.env` file
5. Starting the service

### Option 2: Manual Configuration

1. **Set environment variables:**

```bash
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-app-password
export EMAIL_FROM_ADDRESS=noreply@logistics-platform.com
export EMAIL_FROM_NAME="Logistics Platform"
```

2. **Start the service:**

```bash
mvn spring-boot:run
```

## Configuration

See [`SMTP_SETUP_GUIDE.md`](./SMTP_SETUP_GUIDE.md) for detailed configuration instructions for:
- Gmail
- SendGrid
- AWS SES
- Mailgun
- Office 365
- Custom SMTP servers

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SMTP_HOST` | SMTP server hostname | `smtp.gmail.com` |
| `SMTP_PORT` | SMTP server port | `587` |
| `SMTP_USERNAME` | SMTP username | - |
| `SMTP_PASSWORD` | SMTP password/API key | - |
| `EMAIL_FROM_ADDRESS` | From email address | `noreply@logistics-platform.com` |
| `EMAIL_FROM_NAME` | From display name | `Logistics Platform` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `localhost:9092` |
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/logistics_platform` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `SERVER_PORT` | Service port | `8095` |

## Email Templates

Located in `src/main/resources/templates/`:

- **welcome-email.html**: Sent immediately when onboarding starts
- **trial-reminder-email.html**: Sent 7, 3, and 1 days before trial expiry

## Kafka Topics

### Consumed Topics

- `onboarding.started`: Triggers welcome email
- `onboarding.completed`: Triggers completion email

## Scheduled Jobs

### Trial Reminder Scheduler

- **Schedule**: Daily at 9:00 AM
- **Function**: Sends trial reminder emails
- **Logic**:
  - Queries `tenant_onboarding` table
  - Finds trials expiring in 7, 3, or 1 days
  - Sends personalized reminder emails
  - Updates tracking flags to prevent duplicates

## Testing

### Manual Email Test

1. Start the service with SMTP configured
2. Trigger an onboarding event:

```bash
# Start onboarding via tenant-onboarding-service
curl -X POST http://localhost:8090/api/onboarding/start \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Test Company",
    "companyEmail": "your-test-email@gmail.com",
    "contactPersonName": "John Doe",
    "contactPersonEmail": "your-test-email@gmail.com"
  }'
```

3. Check your inbox for the welcome email

### Check Logs

```bash
tail -f logs/notification-service.log
```

Look for:
```
INFO  c.l.n.service.EmailService - Email sent successfully to: test@example.com
```

## Troubleshooting

See [`SMTP_SETUP_GUIDE.md`](./SMTP_SETUP_GUIDE.md) for detailed troubleshooting.

## Production Checklist

- [ ] Configure production SMTP provider (SendGrid/AWS SES recommended)
- [ ] Set up SPF/DKIM/DMARC records for your domain
- [ ] Enable email bounce/complaint handling
- [ ] Configure monitoring and alerting
- [ ] Test email deliverability
- [ ] Verify trial reminder scheduler runs correctly
- [ ] Configure Kafka with proper retention
- [ ] Set up database backups