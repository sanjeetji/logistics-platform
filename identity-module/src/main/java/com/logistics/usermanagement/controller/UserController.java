package com.logistics.usermanagement.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.usermanagement.entity.User;
import com.logistics.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Management REST Controller
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByTenant(@PathVariable String tenantId) {
        List<User> users = userService.getUsersByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/tenant/{tenantId}/active")
    public ResponseEntity<ApiResponse<List<User>>> getActiveUsersByTenant(@PathVariable String tenantId) {
        List<User> users = userService.getActiveUsersByTenant(tenantId);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "User created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    @GetMapping("/tenant/{tenantId}/count")
    public ResponseEntity<ApiResponse<Long>> countActiveUsers(@PathVariable String tenantId) {
        long count = userService.countActiveUsers(tenantId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
