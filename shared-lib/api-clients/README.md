README.md for api-clients:
markdown
# API Clients Library

External API clients and integrations for the Logistics Platform.

## 🎯 Purpose
Provides pre-configured HTTP clients, Feign interfaces, and integration adapters for external services used across the platform. Ensures consistent API communication patterns, error handling, and retry logic.

## 📁 Package Structure
com.logistics.sharedlib.apiclients
├── config/
│   ├── FeignConfig.java         # Feign client configuration
│   ├── RestTemplateConfig.java  # RestTemplate configuration
│   └── RetryConfig.java         # Retry policies
├── feign/
│   ├── PaymentClient.java       # Payment gateway client
│   ├── SmsClient.java           # SMS gateway client
│   ├── EmailClient.java         # Email service client
│   └── MapsClient.java          # Maps/Geo service client
├── googlemaps/
│   ├── GoogleMapsClient.java    # Google Maps API client
│   ├── DirectionsClient.java    # Directions API wrapper
│   ├── DistanceMatrixClient.java # Distance matrix client
│   └── GeocodingClient.java     # Geocoding client
├── sms/
│   ├── TwilioClient.java        # Twilio SMS client
│   ├── MessagebirdClient.java   # Messagebird client
│   └── SmsProviderFactory.java  # SMS provider factory
├── payment/
│   ├── RazorpayClient.java      # Razorpay payment gateway
│   ├── StripeClient.java        # Stripe payment gateway
│   ├── PaypalClient.java        # PayPal client
│   └── PaymentGatewayFactory.java
├── notification/
│   ├── FirebaseClient.java      # Firebase Cloud Messaging
│   ├── OneSignalClient.java     # OneSignal push notifications
│   └── WebhookClient.java       # Webhook dispatcher
└── models/
├── ApiResponse.java         # Standard API response
├── ApiError.java            # API error model
└── RetryContext.java        # Retry context

## 🛠️ Key Components

### Feign Clients
```java
@FeignClient(
    name = "payment-service",
    url = "${api.clients.payment.base-url}",
    configuration = PaymentClientConfig.class
)
public interface PaymentClient {
    
    @PostMapping("/payments/create")
    ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentRequest request);
    
    @GetMapping("/payments/{paymentId}/status")
    ApiResponse<PaymentStatus> getPaymentStatus(@PathVariable String paymentId);
    
    @PostMapping("/payments/{paymentId}/refund")
    ApiResponse<RefundResponse> refundPayment(
        @PathVariable String paymentId,
        @RequestBody RefundRequest request
    );
}
Google Maps Integration
java
public class GoogleMapsClient {
    
    public DirectionsResponse getDirections(
        String origin, 
        String destination,
        TravelMode mode
    ) {
        // Implementation with retry and circuit breaker
    }
    
    public DistanceMatrixResponse calculateDistanceMatrix(
        List<String> origins,
        List<String> destinations
    ) {
        // Batch distance calculation
    }
    
    public GeocodingResponse geocodeAddress(String address) {
        // Address to coordinates
    }
    
    public ReverseGeocodingResponse reverseGeocode(
        double lat, 
        double lng
    ) {
        // Coordinates to address
    }
}
SMS Clients
java
public class TwilioClient implements SmsProvider {
    
    public SmsResponse sendSms(
        String to, 
        String message,
        String senderId
    ) {
        // Send SMS with Twilio
    }
    
    public SmsResponse sendBulkSms(
        List<String> recipients,
        String message
    ) {
        // Bulk SMS sending
    }
    
    public DeliveryStatus checkDeliveryStatus(String messageId) {
        // Check SMS delivery status
    }
}
Payment Gateway Clients
java
public class RazorpayClient implements PaymentGateway {
    
    public PaymentResponse createOrder(OrderRequest order) {
        // Create payment order
    }
    
    public PaymentResponse capturePayment(String paymentId) {
        // Capture payment
    }
    
    public List<Payment> fetchPayments(
        LocalDateTime from,
        LocalDateTime to
    ) {
        // Fetch payment history
    }
    
    public RefundResponse initiateRefund(
        String paymentId,
        BigDecimal amount
    ) {
        // Initiate refund
    }
}
🚀 Usage Examples
Basic Feign Client Usage
java
@Autowired
private PaymentClient paymentClient;

public PaymentResponse processPayment(Order order) {
    PaymentRequest request = PaymentRequest.builder()
        .amount(order.getTotalAmount())
        .currency("INR")
        .orderId(order.getId())
        .customerEmail(order.getCustomerEmail())
        .build();
    
    ApiResponse<PaymentResponse> response = 
        paymentClient.createPayment(request);
    
    if (response.isSuccess()) {
        return response.getData();
    } else {
        throw new PaymentException(response.getError());
    }
}
Google Maps Integration
java
@Autowired
private GoogleMapsClient mapsClient;

public RouteDetails calculateRoute(
    Location pickup, 
    Location delivery
) {
    DirectionsResponse response = mapsClient.getDirections(
        formatLocation(pickup),
        formatLocation(delivery),
        TravelMode.DRIVING
    );
    
    return RouteDetails.builder()
        .distance(response.getDistance())
        .duration(response.getDuration())
        .polyline(response.getPolyline())
        .steps(response.getSteps())
        .build();
}
SMS Sending
java
@Autowired
private SmsProviderFactory smsFactory;

public void sendOtp(String phoneNumber, String otp) {
    SmsProvider provider = smsFactory.getDefaultProvider();
    String message = String.format(
        "Your OTP for Logistics Platform is %s. Valid for 10 minutes.",
        otp
    );
    
    SmsResponse response = provider.sendSms(
        phoneNumber,
        message,
        "LOGISTICS"
    );
    
    if (!response.isSuccess()) {
        log.error("Failed to send SMS: {}", response.getError());
    }
}
📦 Configuration
application.yml
yaml
api:
  clients:
    payment:
      base-url: https://api.razorpay.com/v1
      api-key: ${RAZORPAY_API_KEY}
      secret-key: ${RAZORPAY_SECRET_KEY}
      timeout: 30000
      max-retries: 3
    
    google-maps:
      api-key: ${GOOGLE_MAPS_API_KEY}
      base-url: https://maps.googleapis.com/maps/api
      timeout: 10000
    
    twilio:
      account-sid: ${TWILIO_ACCOUNT_SID}
      auth-token: ${TWILIO_AUTH_TOKEN}
      phone-number: ${TWILIO_PHONE_NUMBER}
    
    retry:
      max-attempts: 3
      backoff-delay: 1000
      max-backoff-delay: 10000
Resilience4j Circuit Breaker
yaml
resilience4j:
  circuitbreaker:
    instances:
      payment-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
📦 Maven Dependency
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>api-clients</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
🔧 Building
bash
# Build library
cd shared-lib/api-clients
mvn clean install

# Run tests
mvn test

# Generate Javadoc
mvn javadoc:javadoc
🧪 Testing Strategy
Unit tests for all clients

Integration tests with mock servers

Circuit breaker testing

Retry mechanism testing

Error handling scenarios

🚀 Performance Considerations
Connection pooling for HTTP clients

Response caching where appropriate

Asynchronous API calls for non-critical operations

Request batching for bulk operations

🔒 Security Notes
API keys stored in environment variables

Encrypted configuration for sensitive data

TLS/SSL for all external communications

Request signing for payment gateways

📝 Best Practices
Always use retry with exponential backoff

Implement circuit breaker pattern

Log API calls with correlation IDs

Handle rate limiting gracefully

Validate all API responses

Use connection timeout and read timeout

Implement health checks for external services

🔄 Updating API Clients
When adding new clients:

Follow existing patterns

Add comprehensive error handling

Include retry and circuit breaker

Add configuration properties

Write integration tests

Update documentation