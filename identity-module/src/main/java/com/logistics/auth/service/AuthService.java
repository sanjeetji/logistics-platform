package com.logistics.auth.service;

import com.logistics.auth.dto.LoginRequest;
import com.logistics.auth.dto.RegisterRequest;
import com.logistics.auth.dto.TokenRefreshRequest;
import com.logistics.auth.dto.TokenRefreshResponse;
import com.logistics.auth.dto.PasswordResetRequest;
import com.logistics.auth.dto.NewPasswordRequest;
import com.logistics.platform.common.dto.users.UserDto;

public interface AuthService {
    UserDto register(RegisterRequest registerRequest);

    String login(LoginRequest loginRequest);

    // New Advanced Features
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);

    void logout(String token);

    UserDto getUserProfile(Long userId);

    void forgotPassword(PasswordResetRequest request);

    void resetPassword(NewPasswordRequest request);

    String switchTenant(com.logistics.auth.dto.SwitchTenantRequest request);

    void changePassword(Long userId, com.logistics.auth.dto.ChangePasswordRequest request);
}
