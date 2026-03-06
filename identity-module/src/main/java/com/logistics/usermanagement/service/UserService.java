package com.logistics.usermanagement.service;

import com.logistics.usermanagement.entity.User;
import com.logistics.usermanagement.entity.UserPreferences;
import com.logistics.usermanagement.repository.UserRepository;
import com.logistics.usermanagement.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Consolidated User Service - handles all user operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository preferencesRepository;

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Cacheable(value = "users", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public List<User> getUsersByTenant(String tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    public List<User> getActiveUsersByTenant(String tenantId) {
        return userRepository.findByTenantIdAndStatus(tenantId, User.UserStatus.ACTIVE);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }
        
        User savedUser = userRepository.save(user);
        
        // Create default preferences
        UserPreferences preferences = UserPreferences.builder()
            .user(savedUser)
            .build();
        preferencesRepository.save(preferences);
        
        log.info("Created user: {} for tenant: {}", savedUser.getEmail(), savedUser.getTenantId());
        return savedUser;
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public User updateUser(Long id, User userUpdate) {
        User existingUser = getUserById(id);
        
        existingUser.setFirstName(userUpdate.getFirstName());
        existingUser.setLastName(userUpdate.getLastName());
        existingUser.setPhoneNumber(userUpdate.getPhoneNumber());
        existingUser.setProfileImageUrl(userUpdate.getProfileImageUrl());
        
        return userRepository.save(existingUser);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void updateLastLogin(Long id) {
        User user = getUserById(id);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Deactivated user: {}", user.getEmail());
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);
        log.info("Deleted user: {}", user.getEmail());
    }

    public long countActiveUsers(String tenantId) {
        return userRepository.countByTenantIdAndStatus(tenantId, User.UserStatus.ACTIVE);
    }
}
