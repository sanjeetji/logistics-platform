package com.logistics.user.service.impl;

import com.logistics.platform.common.dto.users.UserDto;
import com.logistics.platform.common.exceptions.types.ResourceNotFoundException;
import com.logistics.user.model.User;
import com.logistics.user.repository.UserRepository;
import com.logistics.user.service.UserService;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(java.util.UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Override
    public UserDto updateUser(java.util.UUID id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update fields (excluding ID and Email typically, or specific logic)
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        // user.setAddress(userDto.getAddress()); // Assuming DTO has this, if not
        // ignore

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .userType(user.getUserType())
                // .organizationId() // User Service might not know this directly if valid for
                // B2C, or it comes from UserTenant
                .active(true) // Default
                .build();
    }
}
