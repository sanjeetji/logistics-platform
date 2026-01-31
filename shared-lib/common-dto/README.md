### **11. `shared-lib/common-dto/README.md`**
```markdown
# Common DTO Library

Shared Data Transfer Objects for consistent API contracts across the Logistics Platform.

## 🎯 Purpose
Provides standardized request/response objects, validation annotations, and serialization rules to ensure consistency across all microservices' APIs.

## 📁 Package Structure
com.logistics.common.dto
├── auth/ # Authentication DTOs
│ ├── LoginRequest.java
│ ├── LoginResponse.java
│ ├── RegisterRequest.java
│ ├── TokenResponse.java
│ └── UserDTO.java
├── order/ # Order DTOs
│ ├── OrderRequest.java
│ ├── OrderResponse.java
│ ├── OrderItemDTO.java
│ ├── OrderStatus.java
│ └── OrderSearchRequest.java
├── user/ # User DTOs
│ ├── UserCreateRequest.java
│ ├── UserUpdateRequest.java
│ ├── UserResponse.java
│ └── ProfileDTO.java
├── tenant/ # Tenant DTOs
│ ├── TenantRequest.java
│ ├── TenantResponse.java
│ ├── TenantConfigDTO.java
│ └── OnboardingRequest.java
├── tracking/ # Tracking DTOs
│ ├── LocationDTO.java
│ ├── TrackingRequest.java
│ ├── TrackingResponse.java
│ └── GeofenceDTO.java
├── common/ # Common DTOs
│ ├── ApiResponse.java
│ ├── ErrorResponse.java
│ ├── PaginationRequest.java
│ ├── PaginationResponse.java
│ └── Metadata.java
└── enums/ # Enumerations
├── OrderType.java
├── UserRole.java
├── TenantPlan.java
└── DeliveryStatus.java

text

## 📦 Key DTOs

### ApiResponse (Standard Response Wrapper)
```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String requestId;
    private LocalDateTime timestamp;
    
    // Static factory methods
    public static <T> ApiResponse<T> success(T data) { ... }
    public static <T> ApiResponse<T> error(String message) { ... }
}
ErrorResponse (Standard Error Format)
java
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> errors;
    
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
Pagination Support
java
public class PaginationRequest {
    private int page = 0;
    private int size = 20;
    private String sortBy;
    private SortDirection direction = SortDirection.ASC;
    
    public enum SortDirection {
        ASC, DESC
    }
}

public class PaginationResponse<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
}
🔒 Validation Annotations
Custom validation annotations for domain-specific validation:

java
// Custom validation example
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface ValidPhoneNumber {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
🎨 Serialization Configuration
Jackson annotations for JSON serialization

Custom serializers for special types

Date/time formatting (ISO 8601)

Null value handling

Pretty printing for development

🚀 Usage
Maven Dependency
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>common-dto</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
In Controller
java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Implementation
    }
    
    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile() {
        // Implementation
    }
}
Validation Example
java
public class OrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<OrderItemDTO> items;
    
    @NotNull(message = "Pickup address is required")
    @Valid
    private AddressDTO pickupAddress;
    
    @NotNull(message = "Delivery address is required")
    @Valid
    private AddressDTO deliveryAddress;
    
    @Future(message = "Delivery time must be in the future")
    private LocalDateTime scheduledDeliveryTime;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter code")
    private String currency = "INR";
}
📝 Best Practices
Immutable DTOs: Use @Value or final fields with constructors

Builder Pattern: Provide builders for complex DTOs

Validation Groups: Use validation groups for different scenarios

Documentation: Include Javadoc for all public DTOs

Versioning: Consider versioning for breaking changes

🔧 Building and Testing
Build
bash
cd shared-lib/common-dto
mvn clean install
Generate Documentation
bash
mvn javadoc:javadoc
Run Tests
bash
mvn test
📊 Versioning Strategy
Major version: Breaking changes

Minor version: New features, backward compatible

Patch version: Bug fixes, backward compatible

🔄 Migration Guide
When making breaking changes:

Deprecate old fields/methods

Provide migration path in documentation

Maintain backward compatibility when possible

Communicate changes to all service teams

📚 Related Documentation
API Design Guidelines

Validation Framework Guide

Serialization Best Practices