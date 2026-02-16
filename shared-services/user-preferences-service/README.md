# User Preferences Service

User preferences and settings management service.

## Overview

This service manages user preferences for notifications, themes, language, communication, privacy, and accessibility settings. It provides APIs for users to customize their experience across the platform.

## Features

- ✅ **Notification Preferences** - Email, SMS, push, quiet hours
- ✅ **Theme Customization** - Light/dark mode, colors, density
- ✅ **Language Settings** - Multi-language, timezone, date/time formats
- ✅ **Communication Preferences** - Marketing opt-in, email frequency
- ✅ **Privacy Controls** - Data sharing, tracking, analytics
- ✅ **Accessibility** - Screen reader, high contrast, keyboard navigation
- ✅ **Default Preferences** - Auto-created on first access
- ✅ **Partial Updates** - Update only specific fields
- ✅ **Event Publishing** - Kafka events on preference changes

## API Endpoints

### Main Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/preferences/{userId}` | GET | Get user preferences |
| `/api/preferences/{userId}` | PUT | Update preferences (partial) |
| `/api/preferences/{userId}/reset` | POST | Reset to defaults |
| `/api/preferences/{userId}` | DELETE | Delete preferences |

### Category-Specific Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/preferences/{userId}/notifications` | GET | Get notification preferences |
| `/api/preferences/{userId}/theme` | GET | Get theme preferences |
| `/api/preferences/{userId}/privacy` | GET | Get privacy settings |

## Usage Examples

### Get User Preferences
```bash
curl http://localhost:8096/api/preferences/123
```

### Update Notification Preferences
```bash
curl -X PUT http://localhost:8096/api/preferences/123 \
  -H "Content-Type: application/json" \
  -d '{
    "emailNotifications": false,
    "quietHoursStart": "22:00:00",
    "quietHoursEnd": "08:00:00"
  }'
```

### Update Theme
```bash
curl -X PUT http://localhost:8096/api/preferences/123 \
  -H "Content-Type: application/json" \
  -d '{
    "theme": "DARK",
    "colorScheme": "BLUE",
    "fontSize": "LARGE"
  }'
```

### Update Language
```bash
curl -X PUT http://localhost:8096/api/preferences/123 \
  -H "Content-Type: application/json" \
  -d '{
    "preferredLanguage": "es",
    "timezone": "America/New_York",
    "dateFormat": "DD/MM/YYYY"
  }'
```

### Reset to Defaults
```bash
curl -X POST http://localhost:8096/api/preferences/123/reset
```

## Preference Categories

### 1. Notification Preferences
- Email, SMS, Push toggles
- Quiet hours (start/end time)
- Per-notification-type toggles (order, delivery, payment)

### 2. Language & Localization
- Preferred language (ISO 639-1 codes)
- Timezone
- Date format
- Time format (12h/24h)

### 3. Theme Preferences
- Theme mode (LIGHT, DARK, AUTO)
- Color scheme
- Display density (COMPACT, COMFORTABLE, SPACIOUS)
- Font size (SMALL, MEDIUM, LARGE)

### 4. Communication Preferences
- Marketing emails opt-in
- Promotional SMS opt-in
- Newsletter subscription
- Email frequency (REALTIME, DAILY, WEEKLY, MONTHLY)
- Digest emails toggle

### 5. Privacy Settings
- Share data with partners
- Location tracking
- Analytics tracking
- Personalized ads
- Data retention period (days)

### 6. Accessibility
- Screen reader support
- High contrast mode
- Keyboard navigation

## Default Values

When a user's preferences are first created, the following defaults apply:

```yaml
Notifications:
  email: true
  sms: true
  push: true
  orderStatus: true
  delivery: true
  payment: true

Language:
  language: en
  timezone: UTC
  dateFormat: MM/DD/YYYY
  timeFormat: 12h

Theme:
  mode: LIGHT
  colorScheme: DEFAULT
  density: COMFORTABLE
  fontSize: MEDIUM

Communication:
  marketing: false
  promotional: false
  newsletter: false
  frequency: AS_NEEDED

Privacy:
  shareData: false
  trackLocation: true
  analytics: true
  personalizedAds: false
  retentionDays: 365

Accessibility:
  screenReader: false
  highContrast: false
  keyboard: false
```

## Events

### PreferenceChangedEvent

Published to Kafka topic `user-preferences-changed` when preferences are updated.

**Payload:**
```json
{
  "userId": 123,
  "emailNotifications": false,
  "theme": "DARK",
  ...
}
```

## Integration with Other Services

### Notification Service
The notification-service can subscribe to preference changes and respect user notification preferences before sending messages.

### UI Services
Frontend services can fetch user preferences to:
- Apply the selected theme
- Display content in the preferred language
- Honor communication preferences

## Running the Service

### Local Development
```bash
cd shared-services/user-preferences-service
mvn spring-boot:run
```

### Docker
```bash
docker build -t user-preferences-service .
docker run -p 8096:8096 \
  -e DATABASE_URL=jdbc:postgresql://db:5432/logistics_preferences \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  user-preferences-service
```

## Database Schema

```sql
CREATE TABLE user_preferences (
    user_id BIGINT PRIMARY KEY,
    
    -- Notification preferences
    email_notifications BOOLEAN DEFAULT true,
    sms_notifications BOOLEAN DEFAULT true,
    push_notifications BOOLEAN DEFAULT true,
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    order_status_notifications BOOLEAN DEFAULT true,
    delivery_notifications BOOLEAN DEFAULT true,
    payment_notifications BOOLEAN DEFAULT true,
    
    -- Language
    preferred_language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    date_format VARCHAR(20) DEFAULT 'MM/DD/YYYY',
    time_format VARCHAR(10) DEFAULT '12h',
    
    -- Theme
    theme VARCHAR(20) DEFAULT 'LIGHT',
    color_scheme VARCHAR(20) DEFAULT 'DEFAULT',
    display_density VARCHAR(20) DEFAULT 'COMFORTABLE',
    font_size VARCHAR(20) DEFAULT 'MEDIUM',
    
    -- Communication
    marketing_emails BOOLEAN DEFAULT false,
    promotional_sms BOOLEAN DEFAULT false,
    newsletter_subscription BOOLEAN DEFAULT false,
    email_frequency VARCHAR(20) DEFAULT 'AS_NEEDED',
    digest_emails BOOLEAN DEFAULT false,
    
    -- Privacy
    share_data_with_partners BOOLEAN DEFAULT false,
    track_location BOOLEAN DEFAULT true,
    enable_analytics BOOLEAN DEFAULT true,
    allow_personalized_ads BOOLEAN DEFAULT false,
    data_retention_days INT DEFAULT 365,
    
    -- Accessibility
    screen_reader_support BOOLEAN DEFAULT false,
    high_contrast_mode BOOLEAN DEFAULT false,
    keyboard_navigation BOOLEAN DEFAULT false,
    
    -- Metadata
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    version INT DEFAULT 0
);
```

## Configuration

### application.yml
```yaml
spring:
  application:
    name: user-preferences-service
  datasource:
    url: jdbc:postgresql://localhost:5432/logistics_preferences
  kafka:
    bootstrap-servers: localhost:9092

server:
  port: 8096
```

## API Documentation

Once the service is running, access Swagger UI at:
```
http://localhost:8096/swagger-ui.html
```

## Testing

```bash
# Run tests
mvn test

# Integration tests
mvn verify
```

## Monitoring

- Health: `http://localhost:8096/actuator/health`
- Metrics: `http://localhost:8096/actuator/metrics`
- Prometheus: `http://localhost:8096/actuator/prometheus`

## Future Enhancements

- [ ] Preference validation rules
- [ ] Preference history/audit log
- [ ] Bulk preference updates
- [ ] Preference templates
- [ ] A/B testing integration
- [ ] Preference recommendations based on usage patterns
