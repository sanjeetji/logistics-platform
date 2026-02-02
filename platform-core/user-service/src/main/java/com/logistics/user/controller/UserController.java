package com.logistics.user.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.platform.common.dto.users.UserDto;
import com.logistics.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id), "User profile retrieved"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable java.util.UUID id,
            @RequestBody UserDto userDto) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, userDto), "User profile updated"));
    }
}
