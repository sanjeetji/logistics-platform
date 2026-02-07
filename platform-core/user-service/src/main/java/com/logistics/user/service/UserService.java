package com.logistics.user.service;

import com.logistics.platform.common.dto.users.UserDto;

public interface UserService {
    UserDto getUserById(Long id);

    UserDto updateUser(Long id, UserDto userDto);

    void updateUserStatus(Long id, com.logistics.platform.common.dto.enums.UserStatus status);
}
