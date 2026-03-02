-- V20: Add missing indexes for performance optimization

-- Index for doctor's user reference
CREATE INDEX IF NOT EXISTS idx_doctors_user_id ON doctors (user_id);

-- Index for user_roles join table
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles (role_id);

-- Index for services hospital reference
CREATE INDEX IF NOT EXISTS idx_services_hospital_id ON services (hospital_id);
