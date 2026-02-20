package com.logistics.auth.controller;

import com.logistics.auth.dto.LoginRequest;
import com.logistics.auth.dto.RegisterRequest;
import com.logistics.auth.dto.TokenRefreshRequest;
import com.logistics.auth.dto.TokenRefreshResponse;
import com.logistics.auth.service.AuthService;
import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.common.dto.users.UserDto;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(user, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(token, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request), "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody com.logistics.auth.dto.PasswordResetRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset email sent (mocked)"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody com.logistics.auth.dto.NewPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    @PostMapping("/switch-tenant")
    public ResponseEntity<ApiResponse<String>> switchTenant(
            @Valid @RequestBody com.logistics.auth.dto.SwitchTenantRequest request) {
        String token = authService.switchTenant(request);
        return ResponseEntity.ok(ApiResponse.success(token, "Switched tenant successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody com.logistics.auth.dto.ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Assuming UserDetails implementation (User) has the ID, or we look it up
        com.logistics.auth.model.User user = (com.logistics.auth.model.User) userDetails;
        authService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Safe cast if we are sure, or use service to lookup by username
        // userDetails.getUsername() should be the email
        // We can expose a getProfileByEmail or getId from principal if customized

        // For now, let's assume we cast to our User entity which implements UserDetails
        // casting might be risky if security context changes, but standard flow
        // preserves it.
        com.logistics.auth.model.User user = (com.logistics.auth.model.User) userDetails;

        return ResponseEntity.ok(ApiResponse.success(authService.getUserProfile(user.getId()), "User profile"));
    }
}
