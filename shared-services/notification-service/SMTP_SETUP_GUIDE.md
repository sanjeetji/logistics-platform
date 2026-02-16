# SMTP Configuration Guide for Notification Service

This guide explains how to configure SMTP for sending emails in the notification service.

## Quick Start

The notification service uses environment variables for SMTP configuration. You can configure it in three ways:

1. **Environment Variables** (Recommended for production)
2. **Application Properties File** (For development)
3. **Docker/Kubernetes Secrets** (For containerized deployments)

---

## Option 1: Environment Variables (Recommended)

Set these environment variables before starting the service:

```bash
export SMTP_HOST=smtp.your-provider.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@example.com
export SMTP_PASSWORD=your-app-password
export EMAIL_FROM_ADDRESS=noreply@logistics-platform.com
export EMAIL_FROM_NAME="Logistics Platform"
```

---

## Option 2: Popular Email Provider Configurations

### 1. Gmail (For Development/Testing)

> ⚠️ **Important**: You must create an [App Password](https://myaccount.google.com/apppasswords) for Gmail

```bash
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-16-char-app-password
export EMAIL_FROM_ADDRESS=your-email@gmail.com
export EMAIL_FROM_NAME="Logistics Platform"
```

**Steps to create Gmail App Password:**
1. Go to https://myaccount.google.com/apppasswords
2. Select "Mail" and "Other (Custom name)"
3. Enter "Logistics Platform" as the name
4. Copy the 16-character password
5. Use this password in `SMTP_PASSWORD`

---

### 2. SendGrid (Recommended for Production)

SendGrid offers 100 free emails/day, perfect for production use.

```bash
export SMTP_HOST=smtp.sendgrid.net
export SMTP_PORT=587
export SMTP_USERNAME=apikey  # Literally the word "apikey"
export SMTP_PASSWORD=your-sendgrid-api-key
export EMAIL_FROM_ADDRESS=noreply@your-domain.com
export EMAIL_FROM_NAME="Logistics Platform"
```

**Setup Steps:**
1. Sign up at https://sendgrid.com
2. Go to Settings → API Keys
3. Create a new API key with "Mail Send" permissions
4. Copy the API key and use it as `SMTP_PASSWORD`
5. Verify your sender email/domain in SendGrid

**Pricing:** Free tier: 100 emails/day

---

### 3. AWS SES (Amazon Simple Email Service)

Best for high-volume production use with AWS infrastructure.

```bash
export SMTP_HOST=email-smtp.us-east-1.amazonaws.com  # Change region as needed
export SMTP_PORT=587
export SMTP_USERNAME=your-ses-smtp-username
export SMTP_PASSWORD=your-ses-smtp-password
export EMAIL_FROM_ADDRESS=verified-email@your-domain.com
export EMAIL_FROM_NAME="Logistics Platform"
```

**Setup Steps:**
1. Go to AWS SES Console
2. Verify your email address or domain
3. Create SMTP credentials (IAM user)
4. Note the SMTP endpoint for your region
5. Use the SMTP credentials as username/password

**Pricing:** $0.10 per 1,000 emails (after free tier)

---

### 4. Mailgun

Another popular choice for transactional emails.

```bash
export SMTP_HOST=smtp.mailgun.org
export SMTP_PORT=587
export SMTP_USERNAME=postmaster@your-domain.mailgun.org
export SMTP_PASSWORD=your-mailgun-smtp-password
export EMAIL_FROM_ADDRESS=noreply@your-domain.com
export EMAIL_FROM_NAME="Logistics Platform"
```

**Setup Steps:**
1. Sign up at https://www.mailgun.com
2. Add and verify your domain
3. Go to Sending → Domain Settings → SMTP Credentials
4. Use the provided SMTP credentials

**Pricing:** Free tier: 5,000 emails/month for 3 months

---

### 5. Outlook/Office 365

For enterprise users with Office 365.

```bash
export SMTP_HOST=smtp.office365.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@your-company.com
export SMTP_PASSWORD=your-password
export EMAIL_FROM_ADDRESS=your-email@your-company.com
export EMAIL_FROM_NAME="Logistics Platform"
```

---

## Option 3: Local Development Configuration

For local testing without environment variables, create `application-dev.yaml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

notification:
  email:
    from-address: your-email@gmail.com
    from-name: "Logistics Platform Dev"
```

Then run with: `java -jar notification-service.jar --spring.profiles.active=dev`

---

## Option 4: Docker Compose

Add to your `docker-compose.yml`:

```yaml
services:
  notification-service:
    image: notification-service:latest
    environment:
      SMTP_HOST: smtp.sendgrid.net
      SMTP_PORT: 587
      SMTP_USERNAME: apikey
      SMTP_PASSWORD: ${SENDGRID_API_KEY}
      EMAIL_FROM_ADDRESS: noreply@logistics-platform.com
      EMAIL_FROM_NAME: "Logistics Platform"
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      DATABASE_URL: jdbc:postgresql://postgres:5432/logistics_platform
      DATABASE_USERNAME: postgres
      DATABASE_PASSWORD: ${DB_PASSWORD}
```

Create a `.env` file:
```
SENDGRID_API_KEY=your-sendgrid-api-key
DB_PASSWORD=your-db-password
```

---

## Option 5: Kubernetes Secrets

Create a Kubernetes secret:

```bash
kubectl create secret generic notification-smtp \
  --from-literal=smtp-host=smtp.sendgrid.net \
  --from-literal=smtp-port=587 \
  --from-literal=smtp-username=apikey \
  --from-literal=smtp-password=your-sendgrid-api-key \
  --from-literal=email-from=noreply@logistics-platform.com
```

Reference in deployment:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-service
spec:
  template:
    spec:
      containers:
      - name: notification-service
        image: notification-service:latest
        env:
        - name: SMTP_HOST
          valueFrom:
            secretKeyRef:
              name: notification-smtp
              key: smtp-host
        - name: SMTP_PORT
          valueFrom:
            secretKeyRef:
              name: notification-smtp
              key: smtp-port
        - name: SMTP_USERNAME
          valueFrom:
            secretKeyRef:
              name: notification-smtp
              key: smtp-username
        - name: SMTP_PASSWORD
          valueFrom:
            secretKeyRef:
              name: notification-smtp
              key: smtp-password
```

---

## Testing Your Configuration

### 1. Start the Service

```bash
# With environment variables
export SMTP_HOST=smtp.gmail.com
export SMTP_PORT=587
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-app-password

mvn spring-boot:run
```

### 2. Check Logs

Look for successful email sending in logs:
```
INFO  c.l.n.service.EmailService - Email sent successfully to: test@example.com
```

### 3. Test Email Sending

Trigger an onboarding to test:
```bash
curl -X POST http://localhost:8090/api/onboarding/start \
  -H "Content-Type: application/json" \
  -d '{
    "companyName": "Test Company",
    "companyEmail": "test@example.com",
    "contactPersonName": "John Doe",
    "contactPersonEmail": "test@example.com"
  }'
```

Check your inbox for the welcome email!

---

## Troubleshooting

### Common Issues

#### 1. Authentication Failed
```
AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```

**Solutions:**
- For Gmail: Use App Password, not your regular password
- For SendGrid: Use "apikey" as username (literally)
- Check username/password are correct
- Verify 2FA is enabled for Gmail

#### 2. Connection Timeout
```
MessagingException: Could not connect to SMTP host
```

**Solutions:**
- Check firewall/network allows outbound port 587
- Verify SMTP host is correct
- Try port 465 with SSL instead of 587 with TLS

#### 3. Sender Not Verified
```
550 Sender address rejected
```

**Solutions:**
- Verify your email/domain with the provider
- For AWS SES: Move out of sandbox mode
- For SendGrid: Verify sender identity

#### 4. Rate Limiting
```
550 Daily sending quota exceeded
```

**Solutions:**
- Upgrade your email provider plan
- Implement email queuing
- Use a different provider for high volume

---

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** or secrets management
3. **Enable TLS/SSL** for SMTP connections
4. **Rotate passwords** regularly
5. **Use dedicated email accounts** for sending
6. **Monitor email bounces** and complaints
7. **Implement rate limiting** to prevent abuse

---

## Production Recommendations

### For Small Scale (< 1,000 emails/day)
- **Gmail** (free, easy setup)
- **SendGrid Free Tier** (100/day)

### For Medium Scale (1,000 - 50,000 emails/day)
- **SendGrid** ($15-$90/month)
- **Mailgun** ($35/month)

### For Large Scale (> 50,000 emails/day)
- **AWS SES** (most cost-effective)
- **SendGrid Pro** (better deliverability)

### For Enterprise
- **Office 365** (if already using Microsoft)
- **AWS SES** with dedicated IPs
- **SendGrid Enterprise**

---

## Email Deliverability Tips

1. **SPF Record**: Add to your DNS
   ```
   v=spf1 include:_spf.google.com ~all  # For Gmail
   v=spf1 include:sendgrid.net ~all     # For SendGrid
   ```

2. **DKIM**: Enable in your email provider settings

3. **DMARC**: Add to DNS for better reputation
   ```
   v=DMARC1; p=none; rua=mailto:dmarc@your-domain.com
   ```

4. **Custom Domain**: Use your own domain instead of Gmail

5. **Warm Up**: Gradually increase sending volume

---

## Support

For issues specific to:
- **Gmail**: https://support.google.com/mail
- **SendGrid**: https://support.sendgrid.com
- **AWS SES**: https://aws.amazon.com/ses/faqs/
- **Mailgun**: https://help.mailgun.com

For notification service issues, check application logs at:
```
logs/notification-service.log
```
