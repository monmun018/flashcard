package com.app.flashcard.user.repository;

import com.app.flashcard.user.model.UserPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MyBatis Mapper interface for User operations
 * Replaces the JPA UserRepository
 */
@Mapper
public interface UserMapper {
    
    // Basic CRUD operations
    UserPojo findById(@Param("id") Long id);
    
    UserPojo findByUserLoginID(@Param("loginId") String loginId);
    
    UserPojo findByModernUsername(@Param("username") String username);
    
    UserPojo findByEmail(@Param("email") String email);
    
    List<UserPojo> findAll();
    
    int insert(UserPojo user);
    
    int update(UserPojo user);
    
    int deleteById(@Param("id") Long id);
    
    // Existence checks
    boolean existsByUserLoginID(@Param("loginId") String loginId);
    
    boolean existsByModernUsername(@Param("username") String username);
    
    boolean existsByEmail(@Param("email") String email);
    
    // Security related operations
    int updateFailedLoginAttempts(@Param("id") Long id, @Param("attempts") int attempts);
    
    int updateLockedUntil(@Param("id") Long id, @Param("lockedUntil") LocalDateTime lockedUntil);
    
    int updateLastLoginAt(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt);
    
    // User preferences operations
    Map<String, String> findPreferences(@Param("userId") Long userId);
    
    int insertPreference(@Param("userId") Long userId, @Param("key") String key, @Param("value") String value);
    
    int updatePreference(@Param("userId") Long userId, @Param("key") String key, @Param("value") String value);
    
    int deletePreference(@Param("userId") Long userId, @Param("key") String key);
    
    int deleteAllPreferences(@Param("userId") Long userId);
    
    // Advanced queries
    List<UserPojo> findByStatus(@Param("status") String status);
    
    List<UserPojo> findByRole(@Param("role") String role);
    
    List<UserPojo> findLockedUsers();
    
    int countUsers();
    
    int countActiveUsers();
}