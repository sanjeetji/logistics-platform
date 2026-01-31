### **12. `shared-lib/common-utils/README.md`**
```markdown
# Common Utilities Library

Reusable utility classes and helper functions shared across the Logistics Platform.

## 🎯 Purpose
Provides commonly used utility functions, validation helpers, and reusable code patterns to ensure consistency and reduce duplication across all microservices.

## 📁 Package Structure
com.logistics.common.utils
├── DateUtils.java # Date and time utilities
├── StringUtils.java # String manipulation utilities
├── ValidationUtils.java # Validation helper methods
├── PhoneUtils.java # Phone number utilities
├── AddressUtils.java # Address formatting and validation
├── DistanceUtils.java # Distance and coordinate calculations
├── CurrencyUtils.java # Currency formatting and conversion
├── FileUtils.java # File operations utilities
├── JsonUtils.java # JSON processing utilities
├── EncryptionUtils.java # Encryption and decryption utilities
├── NetworkUtils.java # Network-related utilities
├── LoggingUtils.java # Structured logging utilities
├── CollectionUtils.java # Collection manipulation utilities
└── RandomUtils.java # Random value generation

text

## 🛠️ Key Utilities

### DateUtils
```java
public class DateUtils {
    
    // Convert between timezones
    public static ZonedDateTime convertTimezone(
        ZonedDateTime dateTime, 
        ZoneId targetZone
    ) { ... }
    
    // Format dates consistently
    public static String formatIso8601(LocalDateTime dateTime) { ... }
    
    // Calculate business days
    public static LocalDate addBusinessDays(
        LocalDate date, 
        int days
    ) { ... }
    
    // Age calculation
    public static int calculateAge(LocalDate birthDate) { ... }
}
StringUtils
java
public class StringUtils {
    
    // Safe string operations
    public static String safeSubstring(String str, int start, int end) { ... }
    
    // Truncate with ellipsis
    public static String truncate(String str, int maxLength) { ... }
    
    // Generate slugs
    public static String generateSlug(String input) { ... }
    
    // Mask sensitive data
    public static String maskEmail(String email) { ... }
    public static String maskPhone(String phone) { ... }
    public static String maskCardNumber(String cardNumber) { ... }
}
ValidationUtils
java
public class ValidationUtils {
    
    // Email validation
    public static boolean isValidEmail(String email) { ... }
    
    // Phone number validation (international)
    public static boolean isValidPhoneNumber(String phone) { ... }
    
    // PAN card validation (India)
    public static boolean isValidPAN(String pan) { ... }
    
    // GSTIN validation (India)
    public static boolean isValidGSTIN(String gstin) { ... }
    
    // Aadhaar validation (India)
    public static boolean isValidAadhaar(String aadhaar) { ... }
}
PhoneUtils
java
public class PhoneUtils {
    
    // Format phone numbers
    public static String formatPhoneNumber(
        String phone, 
        String countryCode
    ) { ... }
    
    // Extract country code
    public static String extractCountryCode(String phone) { ... }
    
    // Validate and normalize
    public static String normalizePhoneNumber(
        String phone, 
        String defaultCountry
    ) { ... }
    
    // Get carrier information
    public static String getCarrier(String phone) { ... }
}
AddressUtils
java
public class AddressUtils {
    
    // Parse address components
    public static AddressComponents parseAddress(String address) { ... }
    
    // Format address consistently
    public static String formatAddress(Address address) { ... }
    
    // Validate address completeness
    public static boolean isCompleteAddress(Address address) { ... }
    
    // Calculate address similarity
    public static double addressSimilarity(
        String addr1, 
        String addr2
    ) { ... }
}
DistanceUtils
java
public class DistanceUtils {
    
    // Haversine formula for distance
    public static double calculateDistance(
        double lat1, double lon1,
        double lat2, double lon2
    ) { ... }
    
    // Calculate ETA
    public static Duration calculateETA(
        double distanceKm, 
        double speedKmph
    ) { ... }
    
    // Check if point is within radius
    public static boolean isWithinRadius(
        double centerLat, double centerLon,
        double pointLat, double pointLon,
        double radiusKm
    ) { ... }
}
CurrencyUtils
java
public class CurrencyUtils {
    
    // Format currency
    public static String formatCurrency(
        BigDecimal amount, 
        String currencyCode
    ) { ... }
    
    // Convert between currencies
    public static BigDecimal convertCurrency(
        BigDecimal amount,
        String fromCurrency,
        String toCurrency
    ) { ... }
    
    // Round to currency precision
    public static BigDecimal roundToCurrency(
        BigDecimal amount, 
        String currencyCode
    ) { ... }
}
EncryptionUtils
java
public class EncryptionUtils {
    
    // AES encryption/decryption
    public static String encryptAES(String data, String key) { ... }
    public static String decryptAES(String encrypted, String key) { ... }
    
    // RSA encryption/decryption
    public static String encryptRSA(String data, String publicKey) { ... }
    public static String decryptRSA(String encrypted, String privateKey) { ... }
    
    // Hash functions
    public static String hashSHA256(String input) { ... }
    public static String hashMD5(String input) { ... }
    
    // Generate secure random
    public static String generateSecureRandom(int length) { ... }
}
🚀 Usage Examples
Basic Usage
java
import com.logistics.common.utils.DateUtils;
import com.logistics.common.utils.StringUtils;
import com.logistics.common.utils.ValidationUtils;

// Date formatting
String isoDate = DateUtils.formatIso8601(LocalDateTime.now());

// String manipulation
String maskedEmail = StringUtils.maskEmail("user@example.com");
// Returns: u***@example.com

// Validation
if (ValidationUtils.isValidEmail(email)) {
    // Process email
}

// Phone number formatting
String formattedPhone = PhoneUtils.formatPhoneNumber(
    "9876543210", 
    "IN"
);
// Returns: +91 98765 43210
Advanced Usage
java
// Calculate distance between two points
double distance = DistanceUtils.calculateDistance(
    12.9716, 77.5946,  // Bangalore
    13.0827, 80.2707   // Chennai
);
// Returns: ~290 km

// Address parsing
AddressComponents components = AddressUtils.parseAddress(
    "123 Main St, Bengaluru, Karnataka 560001"
);

// Currency conversion (requires rates service)
BigDecimal converted = CurrencyUtils.convertCurrency(
    new BigDecimal("1000"),
    "USD",
    "INR"
);
📦 Maven Dependency
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>common-utils</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
🔧 Building
Build Library
bash
cd shared-lib/common-utils
mvn clean install
Run Tests
bash
mvn test
Generate Javadoc
bash
mvn javadoc:javadoc
🧪 Testing Strategy
Unit tests for all utility methods

Edge case coverage

Performance testing for critical methods

Thread safety verification

🚀 Performance Considerations
Utilities are stateless and thread-safe

Heavy computations are optimized

Caching where appropriate

Lazy initialization for expensive operations

🔒 Security Notes
Use approved encryption algorithms

No hardcoded secrets

Input validation in all public methods

Secure random number generation

📝 Best Practices
Null Safety: All methods handle null inputs gracefully

Immutable: Utility classes are stateless and immutable

Comprehensive Logging: Include appropriate logging

Internationalization: Support multiple locales where applicable

Documentation: Javadoc for all public methods

🔄 Updating Utilities
When adding new utilities:

Follow existing naming conventions

Add comprehensive tests

Update this README

Consider backward compatibility

Get code review from team