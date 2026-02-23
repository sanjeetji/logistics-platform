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
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import com.logistics.auth.dto.RegisterRequest;

@Service
public class AuthServiceImpl implements AuthService {

        private final RefreshTokenService refreshTokenService;
        private final TokenBlacklistService tokenBlacklistService;
        private final com.logistics.auth.repository.PasswordResetTokenRepository passwordResetTokenRepository;
        private final com.logistics.auth.repository.UserTenantRepository userTenantRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final AuthenticationManager authenticationManager;

        public AuthServiceImpl(RefreshTokenService refreshTokenService,
                        TokenBlacklistService tokenBlacklistService,
                        com.logistics.auth.repository.PasswordResetTokenRepository passwordResetTokenRepository,
                        com.logistics.auth.repository.UserTenantRepository userTenantRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtils jwtUtils,
                        AuthenticationManager authenticationManager) {
                this.refreshTokenService = refreshTokenService;
                this.tokenBlacklistService = tokenBlacklistService;
                this.passwordResetTokenRepository = passwordResetTokenRepository;
                this.userTenantRepository = userTenantRepository;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtils = jwtUtils;
                this.authenticationManager = authenticationManager;
        }

        @Override
        @SuppressWarnings("null")
        public UserDto register(RegisterRequest req) {
                if (userRepository.existsByEmail(req.getEmail())) {
                        throw new RuntimeException("Email already in use");
                }

                // RULE 1: SUPER_ADMIN is a platform-level role — must NOT belong to any tenant
                if (req.getUserType() == UserType.SUPER_ADMIN && req.getOrganizationId() != null) {
                        throw new IllegalArgumentException(
                                        "SUPER_ADMIN is a platform-level role and cannot be assigned to a tenant (organizationId must be null).");
                }

                // RULE 2: All tenant-level roles MUST belong to an organization
                if (req.getUserType() != UserType.SUPER_ADMIN && req.getOrganizationId() == null) {
                        throw new IllegalArgumentException(
                                        "Tenant users (" + req.getUserType()
                                                        + ") must be associated with an organization (organizationId is required).");
                }

                User user = User.builder()
                                .firstName(req.getFirstName())
                                .lastName(req.getLastName())
                                .email(req.getEmail())
                                .password(passwordEncoder.encode(req.getPassword()))
                                .userType(req.getUserType())
                                // .organizationId(req.getOrganizationId()) // Removed
                                .active(true)
                                .build();

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

                return UserDto.builder()
                                .id(savedUser.getId())
                                .firstName(savedUser.getFirstName())
                                .lastName(savedUser.getLastName())
                                .email(savedUser.getEmail())
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

                // MVP: Fetch first tenant or use null
                Long orgId = userTenantRepository.findByUserId(user.getId()).stream()
                                .findFirst()
                                .map(com.logistics.auth.model.UserTenant::getOrganizationId)
                                .orElse(null);

                return jwtUtils.generateToken(user.getEmail(),
                                Collections.singletonList("ROLE_" + user.getUserType().name()),
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

                                        String token = jwtUtils.generateToken(user.getEmail(),
                                                        Collections.singletonList("ROLE_" + user.getUserType().name()),
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
                                .userType(user.getUserType())
                                .organizationId(orgId)
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

                // Generate new token with tenant context
                return jwtUtils.generateToken(user.getEmail(),
                                Collections.singletonList("ROLE_" + userTenant.getRole().name()),
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
