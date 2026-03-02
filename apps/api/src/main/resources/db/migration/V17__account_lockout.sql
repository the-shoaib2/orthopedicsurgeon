-- V17: Add account lockout fields to users table
ALTER TABLE users ADD COLUMN failed_login_attempts INT DEFAULT 0;
ALTER TABLE users ADD COLUMN lockout_until TIMESTAMP;
ALTER TABLE users ADD COLUMN password_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Index for email lookups (if not already present, though typically unique constraint creates one)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
