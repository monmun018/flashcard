package com.app.flashcard.shared.security;

import com.app.flashcard.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security UserDetails implementation that wraps our User entity.
 * This class bridges our domain model with Spring Security's authentication system.
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, all users have the same role
        // In future phases, this could be expanded for role-based access control
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getUserPW();
    }

    @Override
    public String getUsername() {
        return user.getUserLoginID();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Get the underlying User entity.
     * This allows controllers to access user data through Spring Security context.
     * 
     * @return User entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Get user ID for convenience
     * 
     * @return User ID
     */
    public int getUserId() {
        return user.getUserID();
    }

    /**
     * Get display name for convenience
     * 
     * @return User name
     */
    public String getDisplayName() {
        return user.getUserName();
    }
}