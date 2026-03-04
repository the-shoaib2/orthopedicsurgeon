-- V7__create_doctors.sql

DROP TABLE IF EXISTS doctor_availability CASCADE;
DROP TABLE IF EXISTS doctor_specializations CASCADE;
DROP TABLE IF EXISTS doctors CASCADE;

CREATE TABLE doctors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id),
    hospital_id UUID REFERENCES hospitals(id),
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    bio TEXT,
    experience_years INTEGER NOT NULL,
    consultation_fee DECIMAL(19, 2) NOT NULL,
    available_for_online BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctors_hospital_id ON doctors(hospital_id);
CREATE INDEX idx_doctors_status ON doctors(status);
CREATE INDEX idx_doctors_specialization ON doctors(specialization);

CREATE TABLE doctor_specializations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id UUID NOT NULL REFERENCES doctors(id),
    specialization VARCHAR(100) NOT NULL
);

CREATE TABLE doctor_availability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id UUID NOT NULL REFERENCES doctors(id),
    day_of_week VARCHAR(20) NOT NULL, -- MONDAY, TUESDAY, etc.
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    max_appointments_per_slot INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctor_availability_doctor_id ON doctor_availability(doctor_id);
