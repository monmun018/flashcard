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
     * 
     * @param username The login ID entered by the user
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Use UserService to find user by login ID
            User user = userService.findByLoginId(username);
            
            // Wrap User entity in UserPrincipal for Spring Security
            return new UserPrincipal(user);
            
        } catch (EntityNotFoundException e) {
            // Convert our custom exception to Spring Security exception
            throw new UsernameNotFoundException("User not found with login ID: " + username, e);
        }
    }
}