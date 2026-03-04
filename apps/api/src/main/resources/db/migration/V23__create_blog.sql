CREATE TYPE blog_post_status AS ENUM
    ('DRAFT', 'PUBLISHED', 'ARCHIVED');

CREATE TABLE IF NOT EXISTS blog_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS blog_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(80) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS blog_posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(300) NOT NULL,
    slug VARCHAR(320) NOT NULL UNIQUE,
    excerpt TEXT,
    content TEXT,
    featured_image_url VARCHAR(500),
    author_id UUID NOT NULL REFERENCES users(id),
    category_id UUID REFERENCES blog_categories(id)
        ON DELETE SET NULL,
    status blog_post_status NOT NULL DEFAULT 'DRAFT',
    is_featured BOOLEAN NOT NULL DEFAULT false,
    view_count INT NOT NULL DEFAULT 0,
    read_time_minutes INT NOT NULL DEFAULT 1,
    meta_title VARCHAR(160),
    meta_description VARCHAR(320),
    meta_keywords VARCHAR(500),
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS blog_post_tags (
    post_id UUID NOT NULL REFERENCES blog_posts(id)
        ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES blog_tags(id)
        ON DELETE CASCADE,
    PRIMARY KEY (post_id, tag_id)
);

CREATE TABLE IF NOT EXISTS blog_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES blog_posts(id)
        ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    guest_name VARCHAR(150),
    guest_email VARCHAR(254),
    content TEXT NOT NULL,
    parent_id UUID REFERENCES blog_comments(id)
        ON DELETE CASCADE,
    is_approved BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_blog_posts_status_date
    ON blog_posts(status, published_at DESC);
CREATE INDEX IF NOT EXISTS idx_blog_posts_slug
    ON blog_posts(slug);
CREATE INDEX IF NOT EXISTS idx_blog_posts_category
    ON blog_posts(category_id, status);
CREATE INDEX IF NOT EXISTS idx_blog_posts_featured
    ON blog_posts(is_featured, status);
CREATE INDEX IF NOT EXISTS idx_blog_posts_author
    ON blog_posts(author_id);
CREATE INDEX IF NOT EXISTS idx_blog_comments_post
    ON blog_comments(post_id, is_approved);
