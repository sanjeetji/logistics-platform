#!/bin/bash

# Quick Start Script for Notification Service with SMTP Configuration
# This script helps you quickly configure and start the notification service

echo "================================================"
echo "Notification Service - SMTP Quick Start"
echo "================================================"
echo ""

# Function to prompt for input with default value
prompt_with_default() {
    local prompt="$1"
    local default="$2"
    local var_name="$3"
    
    read -p "$prompt [$default]: " input
    eval "$var_name=\"${input:-$default}\""
}

# Check if user wants to use a preset configuration
echo "Select your email provider:"
echo "1) Gmail (for development/testing)"
echo "2) SendGrid (recommended for production)"
echo "3) AWS SES"
echo "4) Mailgun"
echo "5) Office 365"
echo "6) Custom SMTP server"
echo ""

read -p "Enter choice [1-6]: " provider_choice

case $provider_choice in
    1)
        echo ""
        echo "=== Gmail Configuration ==="
        echo "⚠️  You need to create an App Password at: https://myaccount.google.com/apppasswords"
        echo ""
        
        export SMTP_HOST="smtp.gmail.com"
        export SMTP_PORT="587"
        
        read -p "Gmail address: " gmail_user
        export SMTP_USERNAME="$gmail_user"
        export EMAIL_FROM_ADDRESS="$gmail_user"
        
        read -sp "Gmail App Password (16 characters): " gmail_pass
        echo ""
        export SMTP_PASSWORD="$gmail_pass"
        
        export EMAIL_FROM_NAME="Logistics Platform"
        ;;
        
    2)
        echo ""
        echo "=== SendGrid Configuration ==="
        echo "Get your API key from: https://app.sendgrid.com/settings/api_keys"
        echo ""
        
        export SMTP_HOST="smtp.sendgrid.net"
        export SMTP_PORT="587"
        export SMTP_USERNAME="apikey"
        
        read -sp "SendGrid API Key: " sendgrid_key
        echo ""
        export SMTP_PASSWORD="$sendgrid_key"
        
        read -p "From email address: " from_email
        export EMAIL_FROM_ADDRESS="$from_email"
        
        export EMAIL_FROM_NAME="Logistics Platform"
        ;;
        
    3)
        echo ""
        echo "=== AWS SES Configuration ==="
        echo ""
        
        read -p "AWS Region (e.g., us-east-1): " aws_region
        export SMTP_HOST="email-smtp.$aws_region.amazonaws.com"
        export SMTP_PORT="587"
        
        read -p "SES SMTP Username: " ses_user
        export SMTP_USERNAME="$ses_user"
        
        read -sp "SES SMTP Password: " ses_pass
        echo ""
        export SMTP_PASSWORD="$ses_pass"
        
        read -p "Verified email address: " verified_email
        export EMAIL_FROM_ADDRESS="$verified_email"
        
        export EMAIL_FROM_NAME="Logistics Platform"
        ;;
        
    4)
        echo ""
        echo "=== Mailgun Configuration ==="
        echo ""
        
        export SMTP_HOST="smtp.mailgun.org"
        export SMTP_PORT="587"
        
        read -p "Mailgun SMTP Username: " mailgun_user
        export SMTP_USERNAME="$mailgun_user"
        
        read -sp "Mailgun SMTP Password: " mailgun_pass
        echo ""
        export SMTP_PASSWORD="$mailgun_pass"
        
        read -p "From email address: " from_email
        export EMAIL_FROM_ADDRESS="$from_email"
        
        export EMAIL_FROM_NAME="Logistics Platform"
        ;;
        
    5)
        echo ""
        echo "=== Office 365 Configuration ==="
        echo ""
        
        export SMTP_HOST="smtp.office365.com"
        export SMTP_PORT="587"
        
        read -p "Office 365 email: " o365_email
        export SMTP_USERNAME="$o365_email"
        export EMAIL_FROM_ADDRESS="$o365_email"
        
        read -sp "Office 365 password: " o365_pass
        echo ""
        export SMTP_PASSWORD="$o365_pass"
        
        export EMAIL_FROM_NAME="Logistics Platform"
        ;;
        
    6)
        echo ""
        echo "=== Custom SMTP Configuration ==="
        echo ""
        
        read -p "SMTP Host: " custom_host
        export SMTP_HOST="$custom_host"
        
        read -p "SMTP Port [587]: " custom_port
        export SMTP_PORT="${custom_port:-587}"
        
        read -p "SMTP Username: " custom_user
        export SMTP_USERNAME="$custom_user"
        
        read -sp "SMTP Password: " custom_pass
        echo ""
        export SMTP_PASSWORD="$custom_pass"
        
        read -p "From email address: " from_email
        export EMAIL_FROM_ADDRESS="$from_email"
        
        read -p "From name [Logistics Platform]: " from_name
        export EMAIL_FROM_NAME="${from_name:-Logistics Platform}"
        ;;
        
    *)
        echo "Invalid choice. Exiting."
        exit 1
        ;;
esac

# Configure other required services
echo ""
echo "=== Additional Configuration ==="
echo ""

prompt_with_default "Kafka Bootstrap Servers" "localhost:9092" KAFKA_BOOTSTRAP_SERVERS
prompt_with_default "Database URL" "jdbc:postgresql://localhost:5432/logistics_platform" DATABASE_URL
prompt_with_default "Database Username" "postgres" DATABASE_USERNAME
read -sp "Database Password [postgres]: " db_pass
echo ""
DATABASE_PASSWORD="${db_pass:-postgres}"
export DATABASE_PASSWORD

prompt_with_default "Server Port" "8095" SERVER_PORT

# Export all variables
export KAFKA_BOOTSTRAP_SERVERS
export DATABASE_URL
export DATABASE_USERNAME
export SERVER_PORT

# Display configuration summary
echo ""
echo "================================================"
echo "Configuration Summary"
echo "================================================"
echo "SMTP Host:        $SMTP_HOST"
echo "SMTP Port:        $SMTP_PORT"
echo "SMTP Username:    $SMTP_USERNAME"
echo "From Email:       $EMAIL_FROM_ADDRESS"
echo "From Name:        $EMAIL_FROM_NAME"
echo "Kafka:            $KAFKA_BOOTSTRAP_SERVERS"
echo "Database:         $DATABASE_URL"
echo "Server Port:      $SERVER_PORT"
echo "================================================"
echo ""

# Ask if user wants to save to .env file
read -p "Save configuration to .env file? (y/n): " save_env

if [[ "$save_env" == "y" || "$save_env" == "Y" ]]; then
    cat > .env << EOF
# SMTP Configuration
SMTP_HOST=$SMTP_HOST
SMTP_PORT=$SMTP_PORT
SMTP_USERNAME=$SMTP_USERNAME
SMTP_PASSWORD=$SMTP_PASSWORD
EMAIL_FROM_ADDRESS=$EMAIL_FROM_ADDRESS
EMAIL_FROM_NAME=$EMAIL_FROM_NAME

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=$KAFKA_BOOTSTRAP_SERVERS

# Database Configuration
DATABASE_URL=$DATABASE_URL
DATABASE_USERNAME=$DATABASE_USERNAME
DATABASE_PASSWORD=$DATABASE_PASSWORD

# Server Configuration
SERVER_PORT=$SERVER_PORT
EOF
    echo "✅ Configuration saved to .env file"
    echo "⚠️  Remember to add .env to .gitignore!"
fi

# Ask if user wants to start the service
echo ""
read -p "Start the notification service now? (y/n): " start_service

if [[ "$start_service" == "y" || "$start_service" == "Y" ]]; then
    echo ""
    echo "Starting notification service..."
    echo ""
    
    # Check if Maven is available
    if command -v mvn &> /dev/null; then
        mvn spring-boot:run
    else
        echo "❌ Maven not found. Please install Maven or run manually."
        echo ""
        echo "To start manually, run:"
        echo "  mvn spring-boot:run"
        echo ""
        echo "Or if you have a JAR file:"
        echo "  java -jar target/notification-service-*.jar"
    fi
else
    echo ""
    echo "================================================"
    echo "To start the service later, run:"
    echo "================================================"
    echo ""
    echo "Option 1: Load from .env file"
    echo "  export \$(cat .env | xargs) && mvn spring-boot:run"
    echo ""
    echo "Option 2: Set variables manually"
    echo "  export SMTP_HOST=$SMTP_HOST"
    echo "  export SMTP_PORT=$SMTP_PORT"
    echo "  export SMTP_USERNAME=$SMTP_USERNAME"
    echo "  export SMTP_PASSWORD=***"
    echo "  mvn spring-boot:run"
    echo ""
    echo "================================================"
fi
