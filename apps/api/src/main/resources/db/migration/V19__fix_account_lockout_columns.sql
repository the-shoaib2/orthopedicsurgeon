-- V19: Ensure account lockout fields exist in users table (Fix for V17/V18 conflict)
ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_login_attempts INT DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS lockout_until TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Index for email lookups
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
