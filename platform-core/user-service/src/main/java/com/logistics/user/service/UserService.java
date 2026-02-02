package com.logistics.user.service;

import com.logistics.platform.common.dto.users.UserDto;

public interface UserService {
    UserDto getUserById(java.util.UUID id);

    UserDto updateUser(java.util.UUID id, UserDto userDto);
}
