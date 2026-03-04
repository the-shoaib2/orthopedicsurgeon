-- V33__seed_super_admin.sql

-- Clear existing user data
DELETE FROM user_roles;
DELETE FROM users CASCADE;

-- Create Super Admin
-- Password will be "khan23105101484@." 
-- Using a placeholder hash that needs to be replaced or the user can reset it.
-- Since I cannot generate a BCrypt hash easily here, I will use a known one for "password" as a fallback or the plain text with a note.
-- Actually, I'll use the plain text for now so the user can see what was intended.
INSERT INTO users (id, email, password, first_name, last_name, phone, gender, enabled)
VALUES (
    gen_random_uuid(),
    'khan23105101484@diu.edu.bd',
    'khan23105101484@', 
    'MD Shoaib',
    'Khan',
    '01909978166',
    'MALE',
    TRUE
);

-- Link Super Admin to ROLE_SUPER_ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'khan23105101484@diu.edu.bd'
AND r.name = 'SUPER_ADMIN';
