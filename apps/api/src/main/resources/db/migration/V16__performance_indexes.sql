-- 🚀 PERFORMANCE: Optimization Indexes for common query patterns

-- Appointment lookups
CREATE INDEX idx_appointments_composite_search ON appointments (doctor_id, appointment_date, status);
CREATE INDEX idx_appointments_patient_date ON appointments (patient_id, appointment_date);
CREATE INDEX idx_appointments_slot_tracking ON appointments (doctor_id, appointment_date, start_time) WHERE status NOT IN ('CANCELLED', 'NO_SHOW');

-- Lab Report lookups
CREATE INDEX idx_lab_reports_patient_status ON lab_reports (patient_id, status);
CREATE INDEX idx_lab_reports_doctor_date ON lab_reports (doctor_id, report_date);

-- Prescription lookups
CREATE INDEX idx_prescriptions_patient_date ON prescriptions (patient_id, created_at);

-- Payment lookups
CREATE INDEX idx_payments_patient_status ON payments (patient_id, status);
CREATE INDEX idx_payments_transaction ON payments (transaction_id);

-- User audits
CREATE INDEX idx_audit_logs_entity_composite ON audit_logs (entity_name, entity_id, created_at DESC);
CREATE INDEX idx_audit_logs_user_date ON audit_logs (performed_by, created_at DESC);
