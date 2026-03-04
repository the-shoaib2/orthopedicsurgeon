-- V9__create_appointments.sql

CREATE TYPE appointment_status AS ENUM (
    'PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED',
    'CANCELLED', 'NO_SHOW', 'RESCHEDULED'
);

CREATE TYPE appointment_type AS ENUM (
    'IN_PERSON', 'ONLINE', 'EMERGENCY'
);

CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL REFERENCES doctors(id),
    service_id UUID NOT NULL REFERENCES services(id),
    hospital_id UUID NOT NULL REFERENCES hospitals(id),
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status appointment_status NOT NULL DEFAULT 'PENDING',
    type appointment_type NOT NULL DEFAULT 'IN_PERSON',
    chief_complaint TEXT NOT NULL,
    notes TEXT,
    cancellation_reason TEXT,
    cancelled_by UUID REFERENCES users(id),
    booked_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_doctor_slot UNIQUE (doctor_id, appointment_date, start_time)
);

-- Note: In a real production system, you might need a more complex exclusion constraint 
-- to handle overlapping time ranges, but for this requirement, the UNIQUE constraint 
-- on start_time is a solid foundation.

CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_appointments_hospital_id ON appointments(hospital_id);
