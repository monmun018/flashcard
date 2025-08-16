package com.app.flashcard.user.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userID;
    
    // Legacy fields (keep for backward compatibility)
    @Column(name = "UserLoginID")
    private String userLoginID;
    @Column(name = "UserPW")
    private String userPW;
    @Column(name = "UserName")
    private String userName;
    @Column(name = "UserAge")
    private Integer userAge;
    @Column(name = "UserMail")
    private String userMail;
    
    // Modern fields (nullable for now to allow gradual migration)
    @Column(name = "username", unique = true)
    private String username;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status = UserStatus.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.USER;
    
    // Security fields
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Preferences (using separate table in real implementation)
    @ElementCollection
    @CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "preference_key")
    @Column(name = "preference_value")
    private Map<String, String> preferences = new HashMap<>();

    // Constructors
    public User() {}
    
    // Legacy constructor (for backward compatibility)
    public User(String userLoginID, String userPW, String userName, int userAge, String userMail) {
        this.userLoginID = userLoginID;
        this.userPW = userPW;
        this.userName = userName;
        this.userAge = userAge;
        this.userMail = userMail;
        // Set modern fields from legacy fields for compatibility
        this.username = userLoginID;
        this.email = userMail;
        this.firstName = userName != null && userName.contains(" ") ? userName.split(" ")[0] : userName;
        this.lastName = userName != null && userName.contains(" ") ? userName.substring(userName.indexOf(" ") + 1) : "";
        // Approximate date of birth from age
        if (userAge > 0) {
            this.dateOfBirth = LocalDate.now().minusYears(userAge);
        }
    }
    
    // Modern constructor
    public User(String username, String email, String firstName, String lastName, LocalDate dateOfBirth, String passwordHash) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.userPW = passwordHash;
        // Set legacy fields for backward compatibility
        this.userLoginID = username;
        this.userMail = email;
        this.userName = firstName + " " + lastName;
        if (dateOfBirth != null) {
            this.userAge = LocalDate.now().getYear() - dateOfBirth.getYear();
        }
    }

    // Legacy getters/setters (keep for backward compatibility)
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUserLoginID() {
        return userLoginID;
    }

    public void setUserLoginID(String userLoginID) {
        this.userLoginID = userLoginID;
        this.username = userLoginID; // Keep in sync
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        // Update modern fields when legacy field changes
        if (userName != null && userName.contains(" ")) {
            this.firstName = userName.split(" ")[0];
            this.lastName = userName.substring(userName.indexOf(" ") + 1);
        } else {
            this.firstName = userName;
            this.lastName = "";
        }
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
        // Update date of birth when age changes
        if (userAge != null && userAge > 0) {
            this.dateOfBirth = LocalDate.now().minusYears(userAge);
        }
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
        this.email = userMail; // Keep in sync
    }

    // Modern getters/setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.userLoginID = username; // Keep legacy field in sync
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.userMail = email; // Keep legacy field in sync
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateUserName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateUserName();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        // Update age when date of birth changes
        if (dateOfBirth != null) {
            this.userAge = LocalDate.now().getYear() - dateOfBirth.getYear();
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Map<String, String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    // Helper methods
    private void updateUserName() {
        if (firstName != null && lastName != null) {
            this.userName = firstName + " " + lastName;
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Legacy method for backward compatibility
    public User setByUserData(String loginID, String password, String name, int age, String email) {
        this.userLoginID = loginID;
        this.userPW = password;
        this.userName = name;
        this.userAge = age;
        this.userMail = email;
        
        // Update modern fields
        this.username = loginID;
        this.email = email;
        setUserName(name); // This will update firstName and lastName
        setUserAge(age); // This will update dateOfBirth
        
        return this;
    }

    // Utility methods for login
    public boolean canLoginWith(String loginIdentifier) {
        return (username != null && username.equals(loginIdentifier)) ||
               (email != null && email.equals(loginIdentifier)) ||
               (userLoginID != null && userLoginID.equals(loginIdentifier));
    }

    public boolean isAccountLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    public boolean isAccountActive() {
        return status == UserStatus.ACTIVE && !isAccountLocked();
    }
}