package com.app.flashcard.user.service;

import com.app.flashcard.user.form.RegistForm;
import com.app.flashcard.user.model.User;
import com.app.flashcard.user.repository.UserRepository;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import com.app.flashcard.shared.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // PasswordEncoder will be injected through method parameters to avoid circular dependency

    /**
     * Authenticate user with loginID and password
     * @param loginID User login ID
     * @param password Plain text password
     * @return Authenticated user
     * @throws EntityNotFoundException if user not found
     * @throws ValidationException if password is incorrect
     */
    public User authenticate(String loginID, String password) {
        Iterator<User> users = userRepository.findByUserLoginID(loginID).iterator();
        if (!users.hasNext()) {
            throw new EntityNotFoundException("User not found with loginID: " + loginID);
        }
        
        User user = users.next();
        // TODO: In Phase 3, we will implement proper password hashing
        // For now, keeping plain text comparison for backward compatibility
        if (!user.getUserPW().equals(password)) {
            throw new ValidationException("Invalid password for user: " + loginID);
        }
        
        return user;
    }

    /**
     * Create a new user from registration form
     * @param form Registration form data
     * @return Created user
     * @throws ValidationException if loginID already exists
     */
    public User createUser(RegistForm form) {
        // Check if loginID already exists
        if (isLoginIDExists(form.getLoginID())) {
            throw new ValidationException("Tài khoản đã tồn tại.");
        }
        
        try {
            User user = new User().setByRegistForm(form);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ValidationException("Đăng ký bị lỗi!");
        }
    }

    /**
     * Check if loginID already exists
     * @param loginID Login ID to check
     * @return true if exists, false otherwise
     */
    public boolean isLoginIDExists(String loginID) {
        return userRepository.findByUserLoginID(loginID).iterator().hasNext();
    }

    /**
     * Update user profile information
     * @param userId User ID to update
     * @param form Registration form with updated data
     * @return Updated user
     * @throws EntityNotFoundException if user not found
     */
    public User updateProfile(int userId, RegistForm form) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        
        User user = userOpt.get();
        user.setUserPW(form.getPw());
        user.setUserName(form.getName());
        user.setUserAge(form.getAge());
        user.setUserMail(form.getMail());
        
        return userRepository.save(user);
    }

    /**
     * Find user by ID
     * @param userID User ID
     * @return User if found
     * @throws EntityNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User findById(int userID) {
        Optional<User> userOpt = userRepository.findById(userID);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found with ID: " + userID);
        }
        return userOpt.get();
    }

    /**
     * Find user by login ID (for Spring Security)
     * @param loginId Login ID
     * @return User if found
     * @throws EntityNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User findByLoginId(String loginId) {
        Iterator<User> users = userRepository.findByUserLoginID(loginId).iterator();
        if (!users.hasNext()) {
            throw new EntityNotFoundException("User not found with loginID: " + loginId);
        }
        return users.next();
    }

    /**
     * Create user with hashed password (for Spring Security)
     * @param form Registration form data
     * @param passwordEncoder Password encoder to use for hashing
     * @return Created user
     * @throws ValidationException if loginID already exists
     */
    public User createUserWithHashedPassword(RegistForm form, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        // Check if loginID already exists
        if (isLoginIDExists(form.getLoginID())) {
            throw new ValidationException("Tài khoản đã tồn tại.");
        }
        
        try {
            User user = new User().setByRegistForm(form);
            // Hash password before saving
            user.setUserPW(passwordEncoder.encode(form.getPw()));
            return userRepository.save(user);
        } catch (Exception e) {
            throw new ValidationException("Đăng ký bị lỗi!");
        }
    }

    /**
     * Create RegistForm from User for profile display
     * @param user User entity
     * @return RegistForm populated with user data
     */
    @Transactional(readOnly = true)
    public RegistForm createRegistFormFromUser(User user) {
        RegistForm form = new RegistForm();
        form.setLoginID(user.getUserLoginID());
        form.setPw(user.getUserPW());
        form.setName(user.getUserName());
        form.setAge(user.getUserAge());
        form.setMail(user.getUserMail());
        return form;
    }

    // API-specific methods
    
    /**
     * Find user by userLoginID for API
     * @param userLoginID Login ID
     * @return User if found, null otherwise
     */
    @Transactional(readOnly = true)
    public User findByUserLoginID(String userLoginID) {
        Iterator<User> users = userRepository.findByUserLoginID(userLoginID).iterator();
        return users.hasNext() ? users.next() : null;
    }

    /**
     * Check if user exists by userLoginID
     * @param userLoginID Login ID to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUserLoginID(String userLoginID) {
        return userRepository.findByUserLoginID(userLoginID).iterator().hasNext();
    }

    /**
     * Save user entity
     * @param user User to save
     * @return Saved user
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}