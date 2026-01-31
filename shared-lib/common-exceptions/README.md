### **13. `shared-lib/common-exceptions/README.md`**
```markdown
# Common Exceptions Library

Standardized exception hierarchy and error handling for the Logistics Platform.

## 🎯 Purpose
Provides a consistent exception hierarchy, error response format, and global exception handling mechanism across all microservices to ensure uniform error handling and reporting.

## 🏗️ Exception Hierarchy
BaseException (abstract, extends RuntimeException)
├── BusinessException # Business rule violations
│ ├── ValidationException # Input validation errors
│ ├── NotFoundException # Resource not found
│ ├── ConflictException # Resource conflicts
│ ├── UnauthorizedException # Authentication failures
│ └── ForbiddenException # Authorization failures
├── TechnicalException # Technical failures
│ ├── DatabaseException # Database errors
│ ├── ExternalServiceException # 3rd party service errors
│ ├── NetworkException # Network errors
│ └── TimeoutException # Operation timeout
└── SystemException # System-level errors

text

## 📁 Package Structure
com.logistics.common.exceptions
├── BaseException.java # Abstract base exception
├── BusinessException.java # Business rule violations
│ ├── ValidationException.java # Validation errors
│ ├── NotFoundException.java # Resource not found
│ ├── ConflictException.java # Resource conflicts
│ ├── UnauthorizedException.java # Authentication issues
│ └── ForbiddenException.java # Authorization issues
├── TechnicalException.java # Technical failures
│ ├── DatabaseException.java # Database errors
│ ├── ExternalServiceException.java # External service errors
│ ├── NetworkException.java # Network issues
│ └── TimeoutException.java # Timeout errors
├── SystemException.java # System-level errors
├── ErrorCode.java # Standard error codes
├── ErrorResponse.java # Standard error response
└── GlobalExceptionHandler.java # Global exception handler

text

## 📦 Key Classes

### BaseException
```java
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details;
    private final String requestId;
    
    public BaseException(
        ErrorCode errorCode, 
        String message, 
        Map<String, Object> details
    ) { ... }
    
    // Getters and helper methods
    public ErrorCode getErrorCode() { ... }
    public HttpStatus getHttpStatus() { ... }
    public Map<String, Object> getDetails() { ... }
}
ErrorCode Enumeration
java
public enum ErrorCode {
    // Validation errors (400)
    VALIDATION_ERROR("VAL-001", "Validation failed"),
    INVALID_INPUT("VAL-002", "Invalid input provided"),
    
    // Authentication errors (401)
    INVALID_CREDENTIALS("AUTH-001", "Invalid credentials"),
    TOKEN_EXPIRED("AUTH-002", "Token has expired"),
    
    // Authorization errors (403)
    ACCESS_DENIED("AUTH-003", "Access denied"),
    INSUFFICIENT_PERMISSIONS("AUTH-004", "Insufficient permissions"),
    
    // Resource errors (404)
    RESOURCE_NOT_FOUND("RES-001", "Resource not found"),
    USER_NOT_FOUND("RES-002", "User not found"),
    
    // Conflict errors (409)
    RESOURCE_CONFLICT("CON-001", "Resource already exists"),
    CONCURRENT_MODIFICATION("CON-002", "Concurrent modification detected"),
    
    // Technical errors (500)
    DATABASE_ERROR("TEC-001", "Database operation failed"),
    EXTERNAL_SERVICE_ERROR("TEC-002", "External service error"),
    NETWORK_ERROR("TEC-003", "Network error occurred"),
    
    // Business errors (422)
    BUSINESS_RULE_VIOLATION("BUS-001", "Business rule violation"),
    INSUFFICIENT_BALANCE("BUS-002", "Insufficient balance"),
    
    // Rate limiting (429)
    RATE_LIMIT_EXCEEDED("RAT-001", "Rate limit exceeded");
    
    private final String code;
    private final String description;
    
    // Constructor, getters
}
ErrorResponse
java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private String requestId;
    private List<FieldError> errors;
    
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
        private String code;
    }
    
    // Builder pattern
    public static ErrorResponseBuilder builder() { ... }
    
    public static class ErrorResponseBuilder {
        public ErrorResponseBuilder fromException(BaseException ex) { ... }
        public ErrorResponse build() { ... }
    }
}
GlobalExceptionHandler
java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        ValidationException ex, 
        WebRequest request
    ) { ... }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
        NotFoundException ex, 
        WebRequest request
    ) { ... }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
        BusinessException ex, 
        WebRequest request
    ) { ... }
    
    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(
        TechnicalException ex, 
        WebRequest request
    ) { ... }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
        Exception ex, 
        WebRequest request
    ) { ... }
    
    // Handle Spring MVC exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, 
        WebRequest request
    ) { ... }
}
🚀 Usage Examples
Throwing Exceptions
java
import com.logistics.common.exceptions.*;

// Throw validation exception
if (StringUtils.isBlank(email)) {
    throw new ValidationException(
        ErrorCode.VALIDATION_ERROR,
        "Email is required",
        Map.of("field", "email")
    );
}

// Throw not found exception
User user = userRepository.findById(userId)
    .orElseThrow(() -> new NotFoundException(
        ErrorCode.USER_NOT_FOUND,
        "User not found with ID: " + userId,
        Map.of("userId", userId)
    ));

// Throw business exception
if (account.getBalance().compareTo(amount) < 0) {
    throw new BusinessException(
        ErrorCode.INSUFFICIENT_BALANCE,
        "Insufficient balance",
        Map.of(
            "currentBalance", account.getBalance(),
            "requiredAmount", amount
        )
    );
}

// Throw technical exception
try {
    externalService.call();
} catch (IOException e) {
    throw new ExternalServiceException(
        ErrorCode.EXTERNAL_SERVICE_ERROR,
        "Failed to call external service",
        Map.of("service", "payment-gateway"),
        e  // Preserve original cause
    );
}
Custom Exception Example
java
public class InsufficientStockException extends BusinessException {
    
    public InsufficientStockException(
        String productId, 
        int requested, 
        int available
    ) {
        super(
            ErrorCode.INSUFFICIENT_STOCK,
            String.format(
                "Insufficient stock for product %s. Requested: %d, Available: %d",
                productId, requested, available
            ),
            Map.of(
                "productId", productId,
                "requestedQuantity", requested,
                "availableQuantity", available
            )
        );
    }
}

// Usage
throw new InsufficientStockException("PROD-123", 10, 5);
📦 Maven Dependency
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>common-exceptions</artifactId>
<version>1.0.0-SNAPSHOT</version>
</dependency>
🔧 Configuration
Spring Boot Configuration
java
@Configuration
public class ExceptionConfig {
    
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
    
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(
                WebRequest webRequest, 
                ErrorAttributeOptions options
            ) {
                Map<String, Object> errorAttributes = 
                    super.getErrorAttributes(webRequest, options);
                
                // Customize error attributes
                errorAttributes.put("timestamp", LocalDateTime.now());
                errorAttributes.remove("trace");
                
                return errorAttributes;
            }
        };
    }
}
Application Properties
yaml
# Control stack trace exposure
server:
  error:
    include-stacktrace: on_param  # never, always, on_param
    include-message: always
    include-binding-errors: always
    include-exception: false

# Custom error pages (optional)
spring:
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false
📊 Error Response Examples
Validation Error
json
{
  "timestamp": "2026-01-25T14:30:45.123Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VAL-001",
  "message": "Validation failed",
  "path": "/api/v1/users",
  "requestId": "req-123456789",
  "errors": [
    {
      "field": "email",
      "message": "Email is required",
      "rejectedValue": null,
      "code": "NotNull"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters",
      "rejectedValue": "123",
      "code": "Size"
    }
  ]
}
Resource Not Found
json
{
  "timestamp": "2026-01-25T14:30:45.123Z",
  "status": 404,
  "error": "Not Found",
  "code": "RES-001",
  "message": "User not found with ID: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/users/550e8400-e29b-41d4-a716-446655440000",
  "requestId": "req-987654321",
  "details": {
    "userId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
Business Rule Violation
json
{
  "timestamp": "2026-01-25T14:30:45.123Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "code": "BUS-002",
  "message": "Insufficient balance",
  "path": "/api/v1/transactions",
  "requestId": "req-456789123",
  "details": {
    "currentBalance": 1000.00,
    "requiredAmount": 1500.00,
    "currency": "INR"
  }
}
🧪 Testing
Unit Tests for Exceptions
java
@Test
void testNotFoundException() {
    NotFoundException ex = new NotFoundException(
        ErrorCode.USER_NOT_FOUND,
        "User not found",
        Map.of("userId", "123")
    );
    
    assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
    assertTrue(ex.getMessage().contains("User not found"));
}
Integration Tests
java
@SpringBootTest
@AutoConfigureMockMvc
class ExceptionHandlingIntegrationTest {
    
    @Test
    void whenInvalidInput_thenReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"invalid\" }"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VAL-001"))
            .andExpect(jsonPath("$.errors[0].field").value("email"));
    }
}
🔒 Security Considerations
Information Disclosure
Never expose stack traces in production

Sanitize error messages

Avoid revealing internal implementation details

Log errors internally with full details

Rate Limiting Errors
Distinguish between rate limiting and other errors

Include retry-after information in rate limit errors

Log rate limit violations for security monitoring

📈 Monitoring and Alerting
Error Metrics
java
@Component
public class ErrorMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordError(ErrorCode errorCode) {
        meterRegistry.counter("errors.total", 
            "code", errorCode.getCode(),
            "type", errorCode.getType()
        ).increment();
    }
}
Structured Logging
java
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
        Exception ex, 
        WebRequest request
    ) {
        if (ex instanceof BaseException) {
            log.warn("Business exception occurred: {}", ex.getMessage());
        } else {
            log.error("Unexpected error occurred", ex);
        }
        
        // ... handle exception
    }
}
🔄 Migration Guide
From Generic Exceptions
java
// Old way
throw new RuntimeException("User not found");

// New way
throw new NotFoundException(
    ErrorCode.USER_NOT_FOUND,
    "User not found with ID: " + userId,
    Map.of("userId", userId)
);
Adding New Error Codes
Add to ErrorCode enum with unique code

Update error code documentation

Add corresponding exception class if needed

Update tests

📚 Best Practices
Use Specific Exceptions: Prefer specific exceptions over generic ones

Preserve Cause: Always include the original cause when wrapping exceptions

Meaningful Messages: Include relevant context in error messages

Consistent Formatting: Use consistent error response format

Log Appropriately: Business exceptions at WARN, technical at ERROR

Internationalization: Consider i18n for error messages

Document Errors: Document all error codes in API documentation

🔧 Building and Usage
Build Library
bash
cd shared-lib/common-exceptions
mvn clean install
Include in Service
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>common-exceptions</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
Configure in Service
java
@Import(GlobalExceptionHandler.class)
@SpringBootApplication
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}