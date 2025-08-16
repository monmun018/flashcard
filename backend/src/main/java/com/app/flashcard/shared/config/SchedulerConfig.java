package com.app.flashcard.shared.config;

import com.app.flashcard.shared.security.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration for scheduled tasks
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * Clean up expired login attempt entries every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 ms
    public void cleanupExpiredLoginAttempts() {
        loginAttemptService.cleanupExpiredEntries();
    }
}