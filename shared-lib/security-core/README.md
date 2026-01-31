README.md for security-core:
markdown
# Security Core Library

Security utilities, JWT handling, and authentication/authorization components for the Logistics Platform.

## 🎯 Purpose
Provides centralized security infrastructure including JWT token management, authentication filters, authorization checks, and security utilities. Ensures consistent security implementation across all microservices.

## 📁 Package Structure
com.logistics.sharedlib.security
├── config/
│   ├── SecurityConfig.java          # Main security configuration
│   ├── JwtConfig.java               # JWT configuration
│   ├── PasswordConfig.java          # Password encoding config
│   └── CorsConfig.java              # CORS configuration
├── jwt/
│   ├── JwtTokenProvider.java        # JWT token generation/validation
│   ├── JwtTokenValidator.java       # Token validation
│   ├── TokenClaims.java             # JWT claims wrapper
│   └── TokenBlacklistService.java   # Token revocation
├── filters/
│   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   ├── TenantFilter.java            # Tenant context filter
│   ├── RequestLoggingFilter.java    # Security logging
│   └── RateLimitFilter.java         # Rate limiting
├── models/
│   ├── AuthenticatedUser.java       # Authenticated user details
│   ├── TenantContext.java           # Tenant context
│   ├── Permission.java              # Permission model
│   └── Role.java                    # Role model
├── util/
│   ├── PasswordEncoderUtil.java     # Password utilities
│   ├── EncryptionUtil.java          # Encryption utilities
│   ├── SecurityContextUtil.java     # Security context helpers
│   └── IpAddressUtil.java           # IP address utilities
└── exception/
├── JwtTokenException.java       # JWT token exceptions
├── SecurityException.java       # Security exceptions
└── AccessDeniedException.java   # Access denied

## 🛠️ Key Components

### JWT Token Provider
```java
@Component
public class JwtTokenProvider {
    
    public String generateToken(AuthenticatedUser user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("tenantId", user.getTenantId())
            .claim("roles", user.getRoles())
            .claim("permissions", user.getPermissions())
            .setIssuedAt(new Date())
            .setExpiration(Date.from(
                LocalDateTime.now()
                    .plusHours(tokenValidityHours)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            ))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }
    
    public TokenClaims validateToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        
        return TokenClaims.builder()
            .username(claims.getSubject())
            .userId(claims.get("userId", String.class))
            .tenantId(claims.get("tenantId", String.class))
            .roles(claims.get("roles", List.class))
            .permissions(claims.get("permissions", List.class))
            .issuedAt(claims.getIssuedAt())
            .expiresAt(claims.getExpiration())
            .build();
    }
    
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(
            "blacklisted_token:" + hashToken(token)
        );
    }
}
JWT Authentication Filter
java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && jwtTokenValidator.isValid(token)) {
            TokenClaims claims = jwtTokenValidator.validateToken(token);
            
            if (!tokenBlacklistService.isBlacklisted(token)) {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        claims.getUsername(),
                        null,
                        getAuthorities(claims.getPermissions())
                    );
                
                authentication.setDetails(
                    AuthenticatedUser.builder()
                        .id(claims.getUserId())
                        .username(claims.getUsername())
                        .tenantId(claims.getTenantId())
                        .roles(claims.getRoles())
                        .permissions(claims.getPermissions())
                        .token(token)
                        .build()
                );
                
                SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
                
                // Set tenant context
                TenantContext.setCurrentTenant(claims.getTenantId());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && 
            bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
Security Configuration
java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtFilter
    ) throws Exception {
        
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/health"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/tenant/**").hasRole("TENANT_ADMIN")
                .requestMatchers("/api/driver/**").hasRole("DRIVER")
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, 
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
🚀 Usage Examples
Generating JWT Token
java
@Autowired
private JwtTokenProvider tokenProvider;

public String authenticateUser(User user) {
    AuthenticatedUser authUser = AuthenticatedUser.builder()
        .id(user.getId())
        .username(user.getEmail())
        .tenantId(user.getTenantId())
        .roles(user.getRoles())
        .permissions(user.getPermissions())
        .build();
    
    return tokenProvider.generateToken(authUser);
}
Protecting Endpoints
java
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'SHIPMENT', 'READ')")
    public ResponseEntity<ShipmentDto> getShipment(@PathVariable String id) {
        // Only users with READ permission for this shipment can access
    }
    
    @PostMapping
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<ShipmentDto> createShipment(
        @RequestBody CreateShipmentRequest request
    ) {
        // Only dispatchers can create shipments
    }
    
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'SUPERVISOR')")
    public ResponseEntity<Void> assignShipment(
        @PathVariable String id,
        @RequestBody AssignShipmentRequest request
    ) {
        // Only dispatchers and supervisors can assign shipments
    }
}
Password Management
java
@Autowired
private PasswordEncoder passwordEncoder;

public User createUser(CreateUserRequest request) {
    String encodedPassword = passwordEncoder.encode(request.getPassword());
    
    return User.builder()
        .email(request.getEmail())
        .password(encodedPassword)
        .roles(List.of("USER"))
        .build();
}

public boolean validatePassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}
📦 Configuration
application-security.yml
yaml
security:
  jwt:
    secret: ${JWT_SECRET:defaultSecretKeyChangeInProduction}
    validity-hours: 24
    refresh-validity-days: 30
    issuer: logistics-platform
    audience: logistics-services
  
  password:
    encoder-strength: 12
    pepper: ${PASSWORD_PEPPER}
  
  cors:
    allowed-origins:
      - http://localhost:3000
      - https://admin.logistics.com
      - https://customer.logistics.com
    allowed-methods:
      - GET
      - POST
      - PUT
      - DELETE
      - OPTIONS
    allowed-headers:
      - Authorization
      - Content-Type
      - X-Tenant-ID
    max-age: 3600
  
  rate-limit:
    enabled: true
    requests-per-minute: 60
    burst-capacity: 100
Redis Configuration for Token Blacklist
yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
📦 Maven Dependency
xml
<dependency>
    <groupId>com.logistics.platform</groupId>
    <artifactId>security-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
🔧 Building
bash
# Build library
cd shared-lib/security-core
mvn clean install

# Run tests
mvn test

# Security scan
mvn org.owasp:dependency-check-maven:check
🧪 Testing Strategy
Unit tests for all security components

Integration tests for authentication flow

Token validation tests

Password encoding tests

Permission/role tests

🚀 Performance Considerations
Token validation optimized

Redis caching for blacklisted tokens

Connection pooling for Redis

Efficient password hashing

🔒 Security Notes
JWT secret must be strong and rotated periodically

HTTPS required in production

Token blacklisting for logout

Rate limiting to prevent brute force

Input validation for all endpoints

Secure password storage (bcrypt with pepper)

📝 Best Practices
Always validate JWT tokens server-side

Use short-lived access tokens

Implement refresh token rotation

Log all authentication attempts

Monitor for suspicious activities

Regular security audits

Keep dependencies updated

🔄 Updating Security Library
When making changes:

Review security implications

Update token structure if needed

Maintain backward compatibility

Update documentation

Security team review required