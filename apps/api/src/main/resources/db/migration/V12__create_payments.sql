-- V12__create_payments.sql

CREATE TYPE payment_method AS ENUM ('CASH', 'CARD', 'INSURANCE', 'ONLINE', 'BANK_TRANSFER');
CREATE TYPE payment_status AS ENUM ('PENDING', 'PAID', 'PARTIALLY_PAID', 'REFUNDED', 'CANCELLED', 'FAILED');

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    appointment_id UUID UNIQUE REFERENCES appointments(id),
    amount DECIMAL(19, 2) NOT NULL,
    discount DECIMAL(19, 2) DEFAULT 0,
    tax DECIMAL(19, 2) DEFAULT 0,
    total_amount DECIMAL(19, 2) NOT NULL,
    payment_method payment_method NOT NULL,
    payment_status payment_status NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    gateway_response JSONB,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    paid_at TIMESTAMP,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL REFERENCES payments(id),
    invoice_url TEXT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    sent_to_email VARCHAR(100)
);

CREATE INDEX idx_payments_patient_id ON payments(patient_id);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_payments_appointment_id ON payments(appointment_id);
CREATE INDEX idx_payments_invoice ON payments(invoice_number);
