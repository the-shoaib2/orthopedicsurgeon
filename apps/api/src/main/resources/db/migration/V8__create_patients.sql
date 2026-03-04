-- V8__create_patients.sql

DROP TABLE IF EXISTS patient_medical_conditions CASCADE;
DROP TABLE IF EXISTS patient_allergies CASCADE;
DROP TABLE IF EXISTS patients CASCADE;

CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id),
    blood_group VARCHAR(5),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    insurance_provider VARCHAR(100),
    insurance_number VARCHAR(50),
    medical_history_notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patients_user_id ON patients(user_id);
CREATE INDEX idx_patients_status ON patients(status);
CREATE INDEX idx_patients_blood_group ON patients(blood_group);

CREATE TABLE patient_allergies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    allergy VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL -- MILD, MODERATE, SEVERE
);

CREATE TABLE patient_medical_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    condition VARCHAR(100) NOT NULL,
    diagnosed_date DATE,
    is_active BOOLEAN DEFAULT TRUE
);
