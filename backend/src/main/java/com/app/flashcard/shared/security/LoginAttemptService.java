package com.app.flashcard.shared.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service to track and limit login attempts to prevent brute force attacks
 */
@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;
    
    // Store login attempts by IP address
    private final ConcurrentMap<String, AttemptInfo> attemptCache = new ConcurrentHashMap<>();
    
    /**
     * Check if an IP address is blocked due to too many failed attempts
     */
    public boolean isBlocked(String ipAddress) {
        AttemptInfo info = attemptCache.get(ipAddress);
        if (info == null) {
            return false;
        }
        
        // Check if block has expired
        if (info.blockedUntil != null && LocalDateTime.now().isAfter(info.blockedUntil)) {
            // Block expired, reset attempts
            attemptCache.remove(ipAddress);
            return false;
        }
        
        return info.blockedUntil != null;
    }
    
    /**
     * Register a failed login attempt
     */
    public void registerFailedAttempt(String ipAddress) {
        AttemptInfo info = attemptCache.computeIfAbsent(ipAddress, k -> new AttemptInfo());
        
        info.attempts++;
        info.lastAttempt = LocalDateTime.now();
        
        // Block IP if max attempts reached
        if (info.attempts >= MAX_ATTEMPTS) {
            info.blockedUntil = LocalDateTime.now().plus(BLOCK_DURATION_MINUTES, ChronoUnit.MINUTES);
        }
    }
    
    /**
     * Register a successful login (reset attempts for this IP)
     */
    public void registerSuccessfulAttempt(String ipAddress) {
        attemptCache.remove(ipAddress);
    }
    
    /**
     * Get remaining attempts before block
     */
    public int getRemainingAttempts(String ipAddress) {
        AttemptInfo info = attemptCache.get(ipAddress);
        if (info == null) {
            return MAX_ATTEMPTS;
        }
        
        if (isBlocked(ipAddress)) {
            return 0;
        }
        
        return Math.max(0, MAX_ATTEMPTS - info.attempts);
    }
    
    /**
     * Get minutes until block expires
     */
    public long getBlockRemainingMinutes(String ipAddress) {
        AttemptInfo info = attemptCache.get(ipAddress);
        if (info == null || info.blockedUntil == null) {
            return 0;
        }
        
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), info.blockedUntil);
    }
    
    /**
     * Clean up expired entries (should be called periodically)
     */
    public void cleanupExpiredEntries() {
        LocalDateTime now = LocalDateTime.now();
        attemptCache.entrySet().removeIf(entry -> {
            AttemptInfo info = entry.getValue();
            // Remove if blocked time has expired or if last attempt was more than 24 hours ago
            return (info.blockedUntil != null && now.isAfter(info.blockedUntil)) ||
                   (info.lastAttempt != null && ChronoUnit.HOURS.between(info.lastAttempt, now) > 24);
        });
    }
    
    /**
     * Internal class to store attempt information
     */
    private static class AttemptInfo {
        int attempts = 0;
        LocalDateTime lastAttempt;
        LocalDateTime blockedUntil;
    }
}