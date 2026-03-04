CREATE TYPE notification_queue_status AS ENUM
    ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    subject VARCHAR(300),
    email_body TEXT,
    sms_body TEXT,
    push_body TEXT,
    variables JSONB DEFAULT '[]',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS notification_queue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    template_id UUID REFERENCES notification_templates(id),
    channel VARCHAR(20) NOT NULL,
    status notification_queue_status NOT NULL DEFAULT 'PENDING',
    payload JSONB DEFAULT '{}',
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    error_message TEXT,
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS search_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    query TEXT NOT NULL,
    filters JSONB DEFAULT '{}',
    result_count INT NOT NULL DEFAULT 0,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS page_views (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page VARCHAR(500) NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    session_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_notif_queue_status
    ON notification_queue(status, scheduled_at);
CREATE INDEX IF NOT EXISTS idx_search_logs_created
    ON search_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_page_views_page
    ON page_views(page, created_at DESC);
