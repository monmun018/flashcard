package com.app.flashcard.shared.security;

import com.app.flashcard.user.model.User;
import com.app.flashcard.user.service.UserService;
import com.app.flashcard.shared.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService that integrates our User entity with Spring Security.
 * This service is called by Spring Security during authentication to load user details.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * Load user by username for Spring Security authentication.
     * Now supports flexible login: username, email, or legacy userLoginID
     * 
     * @param loginIdentifier The login identifier entered by the user (username, email, or legacy loginID)
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        try {
            // Try modern flexible login first
            User user = userService.findByLoginIdentifier(loginIdentifier);
            
            if (user == null) {
                // Fallback to legacy method for backward compatibility
                try {
                    user = userService.findByLoginId(loginIdentifier);
                } catch (EntityNotFoundException e) {
                    throw new UsernameNotFoundException("User not found with login identifier: " + loginIdentifier, e);
                }
            }
            
            // Check if account is active and not locked
            if (!user.isAccountActive()) {
                throw new UsernameNotFoundException("User account is not active: " + loginIdentifier);
            }
            
            // Wrap User entity in UserPrincipal for Spring Security
            return new UserPrincipal(user);
            
        } catch (Exception e) {
            // Convert any exception to Spring Security exception
            throw new UsernameNotFoundException("User not found with login identifier: " + loginIdentifier, e);
        }
    }
}