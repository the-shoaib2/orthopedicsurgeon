-- V13__create_notifications.sql

CREATE TYPE notification_type AS ENUM (
    'APPOINTMENT_BOOKED', 'APPOINTMENT_CONFIRMED',
    'APPOINTMENT_CANCELLED', 'APPOINTMENT_REMINDER', 'PRESCRIPTION_READY',
    'LAB_REPORT_READY', 'PAYMENT_DUE', 'PAYMENT_RECEIVED', 'SYSTEM'
);

CREATE TYPE notification_channel AS ENUM ('IN_APP', 'EMAIL', 'SMS', 'PUSH');

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    type notification_type NOT NULL,
    reference_id UUID, -- ID of the related entity (appointment, etc)
    reference_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    channel notification_channel NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_type ON notifications(type);
