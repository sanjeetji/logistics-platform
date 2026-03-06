package com.logistics.auth.service.impl;

import com.logistics.auth.dto.LoginRequest;
import com.logistics.auth.model.User;
import com.logistics.auth.repository.UserRepository;
import com.logistics.auth.service.AuthService;
import com.logistics.platform.common.dto.enums.UserType;
import com.logistics.platform.common.dto.users.UserDto;
import com.logistics.platform.security.jwt.JwtUtils;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.logistics.auth.dto.TokenRefreshRequest;
import com.logistics.auth.dto.TokenRefreshResponse;
import com.logistics.auth.model.RefreshToken;
import com.logistics.auth.model.PasswordResetToken;
import com.logistics.auth.service.RefreshTokenService;
import com.logistics.auth.service.TokenBlacklistService;
import java.util.Objects;
import java.util.UUID;

import com.logistics.auth.dto.RegisterRequest;

import com.logistics.auth.service.PhoneVerificationService;

@Service
public class AuthServiceImpl implements AuthService {

        private final PhoneVerificationService phoneVerificationService;
        private final RefreshTokenService refreshTokenService;
        private final TokenBlacklistService tokenBlacklistService;
        private final com.logistics.auth.repository.PasswordResetTokenRepository passwordResetTokenRepository;
        private final com.logistics.auth.repository.UserTenantRepository userTenantRepository;
        private final com.logistics.usermanagement.repository.RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        public AuthServiceImpl(RefreshTokenService refreshTokenService,
                        TokenBlacklistService tokenBlacklistService,
                        com.logistics.auth.repository.PasswordResetTokenRepository passwordResetTokenRepository,
                        com.logistics.auth.repository.UserTenantRepository userTenantRepository,
                        UserRepository userRepository,
                        com.logistics.usermanagement.repository.RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtils jwtUtils,
                        AuthenticationManager authenticationManager,
                        PhoneVerificationService phoneVerificationService) {
                this.refreshTokenService = refreshTokenService;
                this.tokenBlacklistService = tokenBlacklistService;
                this.passwordResetTokenRepository = passwordResetTokenRepository;
                this.userTenantRepository = userTenantRepository;
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtils = jwtUtils;
                this.authenticationManager = authenticationManager;
                this.phoneVerificationService = phoneVerificationService;
        }

        @Override
        @SuppressWarnings("null")
        public UserDto register(RegisterRequest req) {
                if (userRepository.existsByEmail(req.getEmail())) {
                        throw new IllegalArgumentException("Email already in use");
                }

                // RULE 1: SUPER_ADMIN is a platform-level role — must NOT belong to any tenant
                if (req.getUserType() == UserType.SUPER_ADMIN && req.getOrganizationId() != null) {
                        throw new IllegalArgumentException(
                                        "SUPER_ADMIN is a platform-level role and cannot be assigned to a tenant (organizationId must be null).");
                }

                // RULE 2: All tenant-level roles MUST belong to an organization
                if (req.getUserType() != UserType.SUPER_ADMIN
                                && req.getUserType() != UserType.USER
                                && req.getUserType() != UserType.DRIVER
                                && req.getOrganizationId() == null) {
                        throw new IllegalArgumentException(
                                        "Tenant users (" + req.getUserType()
                                                        + ") must be associated with an organization (organizationId is required).");
                }

                // RULE 3: Enforce Phone Verification for Customers and Drivers
                if (req.getUserType() == UserType.CUSTOMER || req.getUserType() == UserType.DRIVER) {
                        if (req.getPhone() == null || req.getPhone().isEmpty()) {
                                throw new IllegalArgumentException(
                                                "Phone number is required for " + req.getUserType() + " registration.");
                        }
                        if (!phoneVerificationService.isPhoneVerified(req.getPhone())) {
                                throw new IllegalArgumentException("Phone number " + req.getPhone()
                                                + " has not been verified. Please verify OTP first.");
                        }
                }

                User user = User.builder()
                                .firstName(req.getFirstName())
                                .lastName(req.getLastName())
                                .email(req.getEmail())
                                .phone(req.getPhone())
                                .password(passwordEncoder.encode(req.getPassword()))
                                .userType(req.getUserType())
                                .tenantId(req.getOrganizationId() != null ? String.valueOf(req.getOrganizationId())
                                                : "SYSTEM")
                                .status(com.logistics.platform.common.dto.enums.UserStatus.ACTIVE)
                                .active(true)
                                .build();

                // Assign default System Role based on UserType
                com.logistics.usermanagement.entity.Role systemRole = roleRepository
                                .findByNameAndTenantId(req.getUserType().name(), "SYSTEM")
                                .orElseGet(() -> roleRepository
                                                .findByNameAndTenantId("ROLE_" + req.getUserType().name(), "SYSTEM")
                                                .orElse(null));

                if (systemRole != null) {
                        java.util.Set<com.logistics.usermanagement.entity.Role> roles = new java.util.HashSet<>();
                        roles.add(systemRole);
                        user.setRoles(roles);
                }

                User savedUser = Objects.requireNonNull(userRepository.save(user));

                // Create UserTenant if organizationId is present
                if (req.getOrganizationId() != null) {
                        com.logistics.auth.model.UserTenant userTenant = com.logistics.auth.model.UserTenant.builder()
                                        .user(savedUser)
                                        .organizationId(req.getOrganizationId())
                                        .role(req.getUserType()) // Default role for this tenant
                                        .build();
                        userTenantRepository.save(userTenant);
                }

                // Clear verification status after successful registration
                if (req.getUserType() == UserType.CUSTOMER || req.getUserType() == UserType.DRIVER) {
                        phoneVerificationService.clearVerification(req.getPhone());
                }

                return UserDto.builder()
                                .id(savedUser.getId())
                                .firstName(savedUser.getFirstName())
                                .lastName(savedUser.getLastName())
                                .email(savedUser.getEmail())
                                .phone(savedUser.getPhone())
                                .userType(savedUser.getUserType())
                                .organizationId(req.getOrganizationId()) // Return what was requested
                                .status(savedUser.isActive() ? com.logistics.platform.common.dto.enums.UserStatus.ACTIVE
                                                : com.logistics.platform.common.dto.enums.UserStatus.INACTIVE)
                                .build();
        }

        @Override
        public String login(LoginRequest loginRequest) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                                loginRequest.getPassword()));

                User user = (User) authentication.getPrincipal();

                user.setLastLogin(java.time.LocalDateTime.now());
                userRepository.save(user);

                // MVP: Fetch first tenant or use null
                Long orgId = userTenantRepository.findByUserId(user.getId()).stream()
                                .findFirst()
                                .map(com.logistics.auth.model.UserTenant::getOrganizationId)
                                .orElse(null);

                java.util.List<String> roles = new java.util.ArrayList<>();
                java.util.List<String> permissions = new java.util.ArrayList<>();

                roles.add("ROLE_" + user.getUserType().name());
                if (user.getRoles() != null) {
                        for (com.logistics.usermanagement.entity.Role r : user.getRoles()) {
                                roles.add("ROLE_" + r.getName().toUpperCase());
                                if (r.getPermissions() != null) {
                                        for (com.logistics.usermanagement.entity.Permission p : r.getPermissions()) {
                                                permissions.add(p.getName());
                                        }
                                }
                        }
                }

                return jwtUtils.generateToken(user.getEmail(),
                                roles,
                                permissions,
                                orgId, user.getUserType());
        }

        @Override
        public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
                String requestRefreshToken = request.getRefreshToken();

                return refreshTokenService.findByToken(requestRefreshToken)
                                .map(refreshTokenService::verifyExpiration)
                                .map(RefreshToken::getUser)
                                .map(user -> {
                                        // MVP: Fetch first tenant or use null
                                        Long orgId = userTenantRepository.findByUserId(user.getId()).stream()
                                                        .findFirst()
                                                        .map(com.logistics.auth.model.UserTenant::getOrganizationId)
                                                        .orElse(null);

                                        java.util.List<String> roles = new java.util.ArrayList<>();
                                        java.util.List<String> permissions = new java.util.ArrayList<>();

                                        roles.add("ROLE_" + user.getUserType().name());
                                        if (user.getRoles() != null) {
                                                for (com.logistics.usermanagement.entity.Role r : user.getRoles()) {
                                                        roles.add("ROLE_" + r.getName().toUpperCase());
                                                        if (r.getPermissions() != null) {
                                                                for (com.logistics.usermanagement.entity.Permission p : r
                                                                                .getPermissions()) {
                                                                        permissions.add(p.getName());
                                                                }
                                                        }
                                                }
                                        }

                                        String token = jwtUtils.generateToken(user.getEmail(),
                                                        roles,
                                                        permissions,
                                                        orgId, user.getUserType());
                                        return new TokenRefreshResponse(token, requestRefreshToken);
                                })
                                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        }

        @Override
        public void logout(String token) {
                // Extract raw token from "Bearer <token>"
                if (token != null && token.startsWith("Bearer ")) {
                        String jwt = token.substring(7);
                        // Default expiration 24 hours (should match JWT expiration)
                        tokenBlacklistService.blacklistToken(jwt, 86400000);
                }
        }

        @Override
        public UserDto getUserProfile(Long userId) {
                User user = userRepository.findById(Objects.requireNonNull(userId, "User ID must not be null"))
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // MVP: Populate with first tenant ID
                Long orgId = userTenantRepository.findByUserId(user.getId()).stream()
                                .findFirst()
                                .map(com.logistics.auth.model.UserTenant::getOrganizationId)
                                .orElse(null);

                return UserDto.builder()
                                .id(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .userType(user.getUserType())
                                .organizationId(orgId)
                                .lastLogin(user.getLastLogin())
                                .status(user.isActive() ? com.logistics.platform.common.dto.enums.UserStatus.ACTIVE
                                                : com.logistics.platform.common.dto.enums.UserStatus.INACTIVE)
                                .build();
        }

        @Override
        public void forgotPassword(com.logistics.auth.dto.PasswordResetRequest request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found with email: " + request.getEmail()));

                // Create token (valid for 1 hour)
                String token = UUID.randomUUID().toString();

                // Save token
                // Note: In production, check if token exists and update or delete
                PasswordResetToken resetToken = PasswordResetToken.builder()
                                .token(token)
                                .user(user)
                                .expiryDate(java.time.LocalDateTime.now().plusHours(1))
                                .build();

                passwordResetTokenRepository.save(resetToken);

                System.out.println("RESET TOKEN for " + user.getEmail() + ": " + token);
        }

        @Override
        public void resetPassword(com.logistics.auth.dto.NewPasswordRequest request) {
                PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                                .orElseThrow(() -> new RuntimeException("Invalid token"));

                if (resetToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
                        throw new RuntimeException("Token expired");
                }

                User user = resetToken.getUser();
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                Objects.requireNonNull(userRepository.save(user));

                passwordResetTokenRepository.delete(resetToken);
        }

        @Override
        public String switchTenant(com.logistics.auth.dto.SwitchTenantRequest request) {
                // Get current user from SecurityContext
                User user = (User) org.springframework.security.core.context.SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                // Find tenant mapping
                com.logistics.auth.model.UserTenant userTenant = userTenantRepository.findByUserId(user.getId())
                                .stream()
                                .filter(ut -> ut.getOrganizationId().equals(request.getTargetOrganizationId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException(
                                                "User does not belong to organization: "
                                                                + request.getTargetOrganizationId()));

                java.util.List<String> roles = new java.util.ArrayList<>();
                java.util.List<String> permissions = new java.util.ArrayList<>();

                roles.add("ROLE_" + userTenant.getRole().name());
                if (user.getRoles() != null) {
                        for (com.logistics.usermanagement.entity.Role r : user.getRoles()) {
                                roles.add("ROLE_" + r.getName().toUpperCase());
                                if (r.getPermissions() != null) {
                                        for (com.logistics.usermanagement.entity.Permission p : r.getPermissions()) {
                                                permissions.add(p.getName());
                                        }
                                }
                        }
                }

                // Generate new token with tenant context
                return jwtUtils.generateToken(user.getEmail(),
                                roles,
                                permissions,
                                userTenant.getOrganizationId(),
                                userTenant.getRole());
        }

        @Override
        public void changePassword(Long userId, com.logistics.auth.dto.ChangePasswordRequest request) {
                User user = userRepository.findById(Objects.requireNonNull(userId, "User ID must not be null"))
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        throw new RuntimeException("Current password does not match");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                Objects.requireNonNull(userRepository.save(user));
        }
}
