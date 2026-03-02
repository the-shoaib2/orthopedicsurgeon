ALTER TABLE totp_secrets ADD COLUMN IF NOT EXISTS backup_codes TEXT; -- Hashed comma-separated or JSON
