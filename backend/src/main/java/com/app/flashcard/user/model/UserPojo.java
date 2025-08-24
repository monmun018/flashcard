package com.app.flashcard.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.app.flashcard.user.model.UserStatus;
import com.app.flashcard.user.model.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * User POJO for MyBatis - Clean model without JPA annotations
 * Using Lombok to reduce boilerplate code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPojo {
    
    // Primary key
    private Long userID;
    
    // Legacy fields (maintain backward compatibility)
    private String userLoginID;
    private String userPW;
    private String userName;
    private Integer userAge;
    private String userMail;
    
    // Modern fields  
    private String modernUsername;  // Renamed to avoid conflict
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String profilePicture;
    
    // Enums
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Builder.Default
    private UserRole role = UserRole.USER;
    
    // Security fields
    @Builder.Default
    private int failedLoginAttempts = 0;
    
    private LocalDateTime lockedUntil;
    private LocalDateTime lastLoginAt;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Preferences (will be handled separately in MyBatis)
    @Builder.Default
    private Map<String, String> preferences = new HashMap<>();
    
    // Business logic methods
    public boolean canLoginWith(String loginIdentifier) {
        return (modernUsername != null && modernUsername.equals(loginIdentifier)) ||
               (email != null && email.equals(loginIdentifier)) ||
               (userLoginID != null && userLoginID.equals(loginIdentifier));
    }
    
    public boolean isAccountLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }
    
    public boolean isAccountActive() {
        return status == UserStatus.ACTIVE && !isAccountLocked();
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return userName; // fallback to legacy field
    }
    
    // Convenience methods for backward compatibility
    public void syncLegacyFields() {
        if (modernUsername != null) {
            this.userLoginID = modernUsername;
        }
        if (email != null) {
            this.userMail = email;
        }
        if (firstName != null && lastName != null) {
            this.userName = firstName + " " + lastName;
        }
        if (dateOfBirth != null) {
            this.userAge = LocalDate.now().getYear() - dateOfBirth.getYear();
        }
    }
    
    public void syncModernFields() {
        if (userLoginID != null) {
            this.modernUsername = userLoginID;
        }
        if (userMail != null) {
            this.email = userMail;
        }
        if (userName != null) {
            String[] parts = userName.split(" ", 2);
            this.firstName = parts[0];
            this.lastName = parts.length > 1 ? parts[1] : "";
        }
        if (userAge != null && userAge > 0) {
            this.dateOfBirth = LocalDate.now().minusYears(userAge);
        }
    }
}