# 📧 SMTP Configuration Guide

The Logistics Platform requires an SMTP server to send notifications, registration emails, and reports.

## 🛠️ 1. Local Development (MailHog)
For local development, we use **MailHog**, which acts as a mock SMTP server. It captures all outgoing emails without actually sending them to real addresses.

- **SMTP Host**: `mailhog` (Internal) / `localhost` (External)
- **SMTP Port**: `1025`
- **Web UI**: [http://localhost:8025](http://localhost:8025)

### How to use:
1. Start the platform: `docker/scripts/run-platform.sh start`
2. Perform any action that triggers an email (e.g., User Registration).
3. Open [http://localhost:8025](http://localhost:8025) in your browser to see the captured email.

---

## 🚀 2. Production Setup

In production, you should use a reliable second-party email provider like **AWS SES**, **SendGrid**, or **Mailtrap**.

### Requirement:
- A verified domain or email address.
- SMTP Credentials (Host, Port, Username, Password).

### Step-by-Step Production Process:

#### A. Using Environment Variables (Recommended)
Set the following environment variables in your production orchestration (Docker Swarm, K8s, or Systemd):
- `MAIL_HOST`: e.g., `email-smtp.us-east-1.amazonaws.com`
- `MAIL_PORT`: `587` (TLS) or `465` (SSL)
- `MAIL_USERNAME`: Your SMTP username.
- `MAIL_PASSWORD`: Your SMTP password.

#### B. Gmail (For Testing/Small Load)
If you must use Gmail, do NOT use your account password. You must generate an **App Password**:
1. Enable 2-Step Verification on your Google Account.
2. Go to **Security** > **2-Step Verification** > **App Passwords**.
3. Select 'Mail' and 'Other' (Logistics Platform).
4. Use the 16-character code as `MAIL_PASSWORD`.

---

## 🔄 3. Seamless Transition (Dev to Prod)
The platform is designed for **Zero Code Changes** when moving to production. All infrastructure services use Spring's property abstraction.

| Environment Variable | Local (MailHog) | Production (Example) |
|----------------------|-----------------|----------------------|
| `MAIL_HOST` | `mailhog` | `smtp.sendgrid.net` |
| `MAIL_PORT` | `1025` | `587` |
| `MAIL_USERNAME` | `""` | `apikey` |
| `MAIL_PASSWORD` | `""` | `your_real_key_here` |
| `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH` | `false` | `true` |
| `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE` | `false` | `true` |

---

## ✅ 4. Health Check
Once configured, the `mail` component in Actuator will report `UP`:
- **Actuator URL**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- If it fails, check if the `MAIL_HOST` is reachable from within the application container.
