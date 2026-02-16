package com.logistics.preferences.repository;

import com.logistics.preferences.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    
    /**
     * Find preferences by user ID
     */
    Optional<UserPreferences> findByUserId(Long userId);
    
    /**
     * Check if preferences exist for user
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Delete preferences by user ID
     */
    void deleteByUserId(Long userId);
}
