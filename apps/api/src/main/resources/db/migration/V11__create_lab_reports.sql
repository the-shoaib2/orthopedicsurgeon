-- V11__create_lab_reports.sql

CREATE TYPE lab_report_status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'REVIEWED');

CREATE TYPE report_type AS ENUM (
    'BLOOD_TEST', 'XRAY', 'MRI', 'CT_SCAN',
    'ULTRASOUND', 'ECG', 'OTHER'
);

CREATE TABLE lab_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID REFERENCES doctors(id),
    appointment_id UUID REFERENCES appointments(id),
    lab_tech_id UUID REFERENCES users(id),
    report_type report_type NOT NULL,
    report_name VARCHAR(100) NOT NULL,
    file_url TEXT,
    file_name VARCHAR(255),
    file_size BIGINT,
    mime_type VARCHAR(50),
    status lab_report_status NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    reported_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lab_reports_patient_id ON lab_reports(patient_id);
CREATE INDEX idx_lab_reports_doctor_id ON lab_reports(doctor_id);
CREATE INDEX idx_lab_reports_status ON lab_reports(status);
CREATE INDEX idx_lab_reports_appointment_id ON lab_reports(appointment_id);
