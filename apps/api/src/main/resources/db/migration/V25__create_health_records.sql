CREATE TYPE document_type AS ENUM (
    'XRAY', 'MRI', 'CT_SCAN', 'BLOOD_TEST',
    'ECG', 'ULTRASOUND', 'DISCHARGE_SUMMARY',
    'REFERRAL', 'INSURANCE', 'OTHER'
);

CREATE TABLE IF NOT EXISTS vital_signs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id)
        ON DELETE CASCADE,
    recorded_by UUID REFERENCES users(id) ON DELETE SET NULL,
    appointment_id UUID REFERENCES appointments(id)
        ON DELETE SET NULL,
    blood_pressure_systolic INT
        CHECK (blood_pressure_systolic BETWEEN 60 AND 250),
    blood_pressure_diastolic INT
        CHECK (blood_pressure_diastolic BETWEEN 40 AND 150),
    heart_rate INT
        CHECK (heart_rate BETWEEN 30 AND 250),
    temperature DECIMAL(4,1)
        CHECK (temperature BETWEEN 35 AND 42),
    weight DECIMAL(6,2)
        CHECK (weight BETWEEN 1 AND 500),
    height DECIMAL(5,2)
        CHECK (height BETWEEN 50 AND 250),
    bmi DECIMAL(5,2),
    oxygen_saturation INT
        CHECK (oxygen_saturation BETWEEN 50 AND 100),
    notes TEXT,
    recorded_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS medical_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id)
        ON DELETE CASCADE,
    uploaded_by UUID REFERENCES users(id) ON DELETE SET NULL,
    document_type document_type NOT NULL DEFAULT 'OTHER',
    document_name VARCHAR(300) NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    description TEXT,
    document_date DATE,
    is_private BOOLEAN NOT NULL DEFAULT false,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS treatment_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id)
        ON DELETE CASCADE,
    doctor_id UUID REFERENCES doctors(id) ON DELETE SET NULL,
    appointment_id UUID REFERENCES appointments(id)
        ON DELETE SET NULL,
    treatment_type VARCHAR(200) NOT NULL,
    treatment_date DATE NOT NULL,
    description TEXT,
    outcome TEXT,
    follow_up_required BOOLEAN NOT NULL DEFAULT false,
    follow_up_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS patient_timeline (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id)
        ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    reference_id UUID,
    reference_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_vitals_patient_date
    ON vital_signs(patient_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_medical_docs_patient
    ON medical_documents(patient_id, is_deleted);
CREATE INDEX IF NOT EXISTS idx_treatment_patient_date
    ON treatment_history(patient_id, treatment_date DESC);
CREATE INDEX IF NOT EXISTS idx_timeline_patient_date
    ON patient_timeline(patient_id, event_date DESC);
