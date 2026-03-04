CREATE TABLE IF NOT EXISTS site_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key VARCHAR(100) NOT NULL UNIQUE,
    value TEXT,
    category VARCHAR(50) NOT NULL DEFAULT 'general',
    is_public BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS hero_slides (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    subtitle VARCHAR(300),
    description TEXT,
    image_url VARCHAR(500),
    button_text VARCHAR(100),
    button_link VARCHAR(300),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS testimonials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_name VARCHAR(150) NOT NULL,
    patient_avatar VARCHAR(500),
    content TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    doctor_id UUID REFERENCES doctors(id) ON DELETE SET NULL,
    service_id UUID REFERENCES services(id) ON DELETE SET NULL,
    is_verified BOOLEAN NOT NULL DEFAULT false,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS faqs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(100) NOT NULL DEFAULT 'general',
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS team_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    title VARCHAR(200),
    bio TEXT,
    image_url VARCHAR(500),
    specialization VARCHAR(200),
    display_order INT NOT NULL DEFAULT 0,
    show_on_website BOOLEAN NOT NULL DEFAULT true,
    social_links JSONB DEFAULT '{}',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS gallery_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200),
    description TEXT,
    image_url VARCHAR(500) NOT NULL,
    category VARCHAR(100) NOT NULL DEFAULT 'general',
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS awards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(300) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    awarded_by VARCHAR(300),
    award_year INT,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS partners (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    logo_url VARCHAR(500),
    website_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_hero_slides_active
    ON hero_slides(is_active, display_order);
CREATE INDEX IF NOT EXISTS idx_testimonials_featured
    ON testimonials(is_featured, is_verified);
CREATE INDEX IF NOT EXISTS idx_faqs_active
    ON faqs(is_active, category, display_order);
CREATE INDEX IF NOT EXISTS idx_gallery_active
    ON gallery_items(is_active, category);
