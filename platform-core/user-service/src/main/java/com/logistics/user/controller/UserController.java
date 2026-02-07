package com.logistics.user.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.common.dto.users.UserDto;
import com.logistics.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.updateUser(id, userDto),
                "User profile updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(@PathVariable Long id,
            @RequestParam com.logistics.platform.common.dto.enums.UserStatus status) {
        userService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated successfully"));
    }
}
