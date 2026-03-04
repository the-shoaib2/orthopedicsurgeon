CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE contact_message_status AS ENUM
    ('NEW', 'READ', 'REPLIED', 'ARCHIVED');

CREATE TABLE IF NOT EXISTS contact_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone VARCHAR(20),
    subject VARCHAR(300) NOT NULL,
    message TEXT NOT NULL,
    status contact_message_status NOT NULL DEFAULT 'NEW',
    replied_by UUID REFERENCES users(id) ON DELETE SET NULL,
    replied_at TIMESTAMP,
    reply_message TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS newsletter_subscribers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(150),
    is_active BOOLEAN NOT NULL DEFAULT false,
    subscribed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    unsubscribed_at TIMESTAMP,
    token VARCHAR(100) NOT NULL UNIQUE
        DEFAULT encode(gen_random_bytes(32), 'hex')
);

CREATE TABLE IF NOT EXISTS appointment_slots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id UUID NOT NULL REFERENCES doctors(id)
        ON DELETE CASCADE,
    slot_date DATE NOT NULL,
    slot_time TIME NOT NULL,
    is_booked BOOLEAN NOT NULL DEFAULT false,
    appointment_id UUID REFERENCES appointments(id)
        ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (doctor_id, slot_date, slot_time)
);

CREATE TABLE IF NOT EXISTS doctor_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL REFERENCES doctors(id),
    appointment_id UUID UNIQUE REFERENCES appointments(id)
        ON DELETE SET NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    is_verified BOOLEAN NOT NULL DEFAULT false,
    is_published BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_contact_messages_status
    ON contact_messages(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_newsletter_token
    ON newsletter_subscribers(token);
CREATE INDEX IF NOT EXISTS idx_newsletter_email
    ON newsletter_subscribers(email);
CREATE INDEX IF NOT EXISTS idx_slots_doctor_date
    ON appointment_slots(doctor_id, slot_date, is_booked);
CREATE INDEX IF NOT EXISTS idx_reviews_doctor
    ON doctor_reviews(doctor_id, is_published);
CREATE INDEX IF NOT EXISTS idx_reviews_patient
    ON doctor_reviews(patient_id);
