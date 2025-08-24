package com.app.flashcard.user.service;

import com.app.flashcard.user.model.User;
import com.app.flashcard.user.model.UserPojo;
import com.app.flashcard.user.repository.UserMapper;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import com.app.flashcard.shared.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * UserService implementation using MyBatis
 * This service provides both legacy User entity support and new UserPojo support
 * TODO: Currently commented out methods that use Lombok-generated methods
 */
@Service("userServiceMyBatis") 
@Transactional
public class UserServiceMyBatis {

    @Autowired
    private UserMapper userMapper;

    // ================================
    // CRUD Operations using UserPojo
    // ================================
    
    /**
     * Find user by ID using MyBatis
     */
    @Transactional(readOnly = true)
    public UserPojo findById(Long userID) {
        UserPojo user = userMapper.findById(userID);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userID);
        }
        return user;
    }

    /**
     * Find user by userLoginID for backward compatibility
     */
    @Transactional(readOnly = true)
    public UserPojo findByUserLoginID(String userLoginID) {
        return userMapper.findByUserLoginID(userLoginID);
    }

    /**
     * Find user by username (modern field)
     */
    @Transactional(readOnly = true)
    public UserPojo findByModernUsername(String username) {
        return userMapper.findByModernUsername(username);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public UserPojo findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    /**
     * Find user by any login identifier (userLoginID, username, or email)
     */
    @Transactional(readOnly = true)
    public UserPojo findByLoginIdentifier(String loginIdentifier) {
        // Try userLoginID first (legacy)
        UserPojo user = userMapper.findByUserLoginID(loginIdentifier);
        if (user != null) return user;
        
        // Try username  
        user = userMapper.findByModernUsername(loginIdentifier);
        if (user != null) return user;
        
        // Try email
        return userMapper.findByEmail(loginIdentifier);
    }

    /**
     * Save/Update user using MyBatis
     */
    public UserPojo save(UserPojo user) {
        // Ensure legacy and modern fields are in sync
        user.syncLegacyFields();
        user.syncModernFields();
        
        if (user.getUserID() == null) {
            // Set audit fields for new user
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            int inserted = userMapper.insert(user);
            if (inserted == 0) {
                throw new RuntimeException("Failed to insert user");
            }
        } else {
            // Update existing user
            user.setUpdatedAt(LocalDateTime.now());
            int updated = userMapper.update(user);
            if (updated == 0) {
                throw new EntityNotFoundException("User not found for update: " + user.getUserID());
            }
        }
        return user;
    }

    /**
     * Delete user by ID
     */
    public boolean deleteById(Long userID) {
        int deleted = userMapper.deleteById(userID);
        return deleted > 0;
    }

    // ================================
    // Existence Checks
    // ================================
    
    public boolean existsByUserLoginID(String userLoginID) {
        return userMapper.existsByUserLoginID(userLoginID);
    }

    public boolean existsByModernUsername(String username) {
        return userMapper.existsByModernUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    public boolean existsByAnyLoginIdentifier(String identifier) {
        return existsByUserLoginID(identifier) || 
               existsByModernUsername(identifier) || 
               existsByEmail(identifier);
    }

    // ================================
    // Authentication & Security
    // ================================
    
    /**
     * Authenticate user with flexible login identifier
     */
    public UserPojo authenticate(String loginIdentifier, String password) {
        UserPojo user = findByLoginIdentifier(loginIdentifier);
        if (user == null) {
            throw new EntityNotFoundException("User not found with identifier: " + loginIdentifier);
        }
        
        // TODO: Implement proper password verification
        // For now, using plain text comparison for backward compatibility
        if (!user.getUserPW().equals(password)) {
            // Increment failed attempts
            incrementFailedLoginAttempts(user.getUserID());
            throw new ValidationException("Invalid password for user: " + loginIdentifier);
        }
        
        // Reset failed attempts on successful login
        resetFailedLoginAttempts(user.getUserID());
        updateLastLoginAt(user.getUserID(), LocalDateTime.now());
        
        return user;
    }

    /**
     * Create user with plain password (legacy support)
     */
    public UserPojo createUser(String loginID, String password, String name, int age, String email) {
        if (existsByUserLoginID(loginID)) {
            throw new ValidationException("Tài khoản đã tồn tại.");
        }
        
        UserPojo user = UserPojo.builder()
            .userLoginID(loginID)
            .userPW(password)
            .userName(name)
            .userAge(age)
            .userMail(email)
            .modernUsername(loginID) // sync modern field
            .email(email)      // sync modern field
            .build();
            
        // Parse name into firstName/lastName
        if (name != null && name.contains(" ")) {
            String[] parts = name.split(" ", 2);
            user.setFirstName(parts[0]);
            user.setLastName(parts[1]);
        } else {
            user.setFirstName(name);
        }
        
        // Convert age to date of birth
        if (age > 0) {
            user.setDateOfBirth(LocalDate.now().minusYears(age));
        }
        
        try {
            return save(user);
        } catch (Exception e) {
            throw new ValidationException("Đăng ký bị lỗi!");
        }
    }

    /**
     * Create user with hashed password
     */
    public UserPojo createUserWithHashedPassword(String loginID, String password, String name, 
                                                int age, String email, PasswordEncoder passwordEncoder) {
        if (existsByUserLoginID(loginID)) {
            throw new ValidationException("Tài khoản đã tồn tại.");
        }
        
        UserPojo user = UserPojo.builder()
            .userLoginID(loginID)
            .userPW(passwordEncoder.encode(password))
            .userName(name)
            .userAge(age)
            .userMail(email)
            .modernUsername(loginID)
            .email(email)
            .build();
            
        user.syncModernFields();
        
        try {
            return save(user);
        } catch (Exception e) {
            throw new ValidationException("Đăng ký bị lỗi!");
        }
    }

    // ================================
    // Security Operations
    // ================================
    
    public void incrementFailedLoginAttempts(Long userId) {
        UserPojo user = findById(userId);
        int newAttempts = user.getFailedLoginAttempts() + 1;
        userMapper.updateFailedLoginAttempts(userId, newAttempts);
        
        // Lock account if too many failed attempts (e.g., 5 attempts)
        if (newAttempts >= 5) {
            LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(30); // Lock for 30 minutes
            userMapper.updateLockedUntil(userId, lockUntil);
        }
    }
    
    public void resetFailedLoginAttempts(Long userId) {
        userMapper.updateFailedLoginAttempts(userId, 0);
        userMapper.updateLockedUntil(userId, null); // Remove lock
    }
    
    public void updateLastLoginAt(Long userId, LocalDateTime loginTime) {
        userMapper.updateLastLoginAt(userId, loginTime);
    }

    // ================================
    // User Preferences
    // ================================
    
    public Map<String, String> getUserPreferences(Long userId) {
        return userMapper.findPreferences(userId);
    }
    
    public void setUserPreference(Long userId, String key, String value) {
        userMapper.insertPreference(userId, key, value); // Uses UPSERT
    }
    
    public void deleteUserPreference(Long userId, String key) {
        userMapper.deletePreference(userId, key);
    }

    // ================================
    // Backward Compatibility Methods
    // ================================
    
    /**
     * Convert UserPojo to legacy User entity for backward compatibility
     * TODO: Enable after Lombok generates getters/setters for UserPojo
     */
    public User toUserEntity(UserPojo userPojo) {
        if (userPojo == null) return null;
        
        User user = new User();
        // TODO: Implement conversion after Lombok compilation
        /*
        user.setUserID(userPojo.getUserID());
        user.setUserLoginID(userPojo.getUserLoginID());
        user.setUserPW(userPojo.getUserPW());
        user.setUserName(userPojo.getUserName());
        user.setUserAge(userPojo.getUserAge());
        user.setUserMail(userPojo.getUserMail());
        
        // Set modern fields that exist in User entity
        user.setUsername(userPojo.getUsername());
        user.setEmail(userPojo.getEmail());
        user.setFirstName(userPojo.getFirstName());
        user.setLastName(userPojo.getLastName());
        user.setDateOfBirth(userPojo.getDateOfBirth());
        user.setPhoneNumber(userPojo.getPhoneNumber());
        user.setProfilePicture(userPojo.getProfilePicture());
        user.setStatus(userPojo.getStatus());
        user.setRole(userPojo.getRole());
        */
        
        return user;
    }
    
    /**
     * Convert legacy User entity to UserPojo
     * TODO: Enable after Lombok generates builder for UserPojo
     */
    public UserPojo fromUserEntity(User user) {
        if (user == null) return null;
        
        // TODO: Implement conversion after Lombok compilation
        /*
        return UserPojo.builder()
            .userID(user.getUserID())
            .userLoginID(user.getUserLoginID())
            .userPW(user.getUserPW())
            .userName(user.getUserName())
            .userAge(user.getUserAge())
            .userMail(user.getUserMail())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dateOfBirth(user.getDateOfBirth())
            .phoneNumber(user.getPhoneNumber())
            .profilePicture(user.getProfilePicture())
            .status(user.getStatus())
            .role(user.getRole())
            .failedLoginAttempts(user.getFailedLoginAttempts())
            .lockedUntil(user.getLockedUntil())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
        */
        return null; // Temporary
    }
}