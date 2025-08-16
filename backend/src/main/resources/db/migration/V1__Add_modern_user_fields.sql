-- Migration V1: Add modern user fields and restructure user table
-- This migration adds email/username login support and modern user fields

-- Add new columns to existing users table
ALTER TABLE users ADD COLUMN username VARCHAR(50);
ALTER TABLE users ADD COLUMN email VARCHAR(255);
ALTER TABLE users ADD COLUMN first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN last_name VARCHAR(100);
ALTER TABLE users ADD COLUMN date_of_birth DATE;
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
ALTER TABLE users ADD COLUMN profile_picture VARCHAR(500);
ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';
ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';

-- Security fields for rate limiting and audit
ALTER TABLE users ADD COLUMN failed_login_attempts INT DEFAULT 0;
ALTER TABLE users ADD COLUMN locked_until TIMESTAMP;
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP;

-- Audit fields
ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Migrate existing data to new structure
-- Set username to existing UserLoginID for backward compatibility
UPDATE users SET username = UserLoginID WHERE username IS NULL;

-- Set email to existing UserMail for backward compatibility  
UPDATE users SET email = UserMail WHERE email IS NULL;

-- Split existing UserName into first_name and last_name (simple split on space)
UPDATE users 
SET first_name = CASE 
    WHEN POSITION(' ' IN UserName) > 0 THEN LEFT(UserName, POSITION(' ' IN UserName) - 1)
    ELSE UserName 
END,
last_name = CASE 
    WHEN POSITION(' ' IN UserName) > 0 THEN SUBSTRING(UserName FROM POSITION(' ' IN UserName) + 1)
    ELSE ''
END
WHERE first_name IS NULL;

-- Create approximate date of birth from age (current year - age)
UPDATE users 
SET date_of_birth = MAKE_DATE(EXTRACT(YEAR FROM CURRENT_DATE)::int - UserAge, 1, 1)
WHERE date_of_birth IS NULL AND UserAge > 0;

-- Add constraints after data migration (commented out for gradual migration)
-- ALTER TABLE users ALTER COLUMN username SET NOT NULL;
-- ALTER TABLE users ALTER COLUMN email SET NOT NULL;
-- ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
-- ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;
-- ALTER TABLE users ALTER COLUMN date_of_birth SET NOT NULL;

-- Add unique constraints
ALTER TABLE users ADD CONSTRAINT uk_users_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

-- Add indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Create login attempts tracking table
CREATE TABLE login_attempts (
    id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    email_or_username VARCHAR(255),
    success BOOLEAN NOT NULL,
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_agent TEXT
);

-- Add index for login attempts
CREATE INDEX idx_login_attempts_ip_time ON login_attempts(ip_address, attempt_time);
CREATE INDEX idx_login_attempts_user_time ON login_attempts(email_or_username, attempt_time);

-- Create user preferences table
CREATE TABLE user_preferences (
    user_id BIGINT REFERENCES users(UserID),
    preference_key VARCHAR(100),
    preference_value TEXT,
    PRIMARY KEY (user_id, preference_key)
);