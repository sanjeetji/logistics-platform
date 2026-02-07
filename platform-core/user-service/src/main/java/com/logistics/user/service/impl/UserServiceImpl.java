package com.logistics.user.service.impl;

import com.logistics.platform.common.dto.users.UserDto;
import com.logistics.user.mapper.UserMapper;
import com.logistics.user.model.User;
import com.logistics.user.model.UserProfile;
import com.logistics.user.repository.UserRepository;
import com.logistics.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());

        if (user.getProfile() == null) {
            user.setProfile(new UserProfile());
        }

        user.getProfile().setAvatarUrl(userDto.getAvatarUrl());
        user.getProfile().setPreferences(userDto.getPreferences());

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, com.logistics.platform.common.dto.enums.UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setStatus(status);
        userRepository.save(user);
    }
}
