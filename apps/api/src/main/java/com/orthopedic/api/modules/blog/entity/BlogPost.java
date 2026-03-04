package com.orthopedic.api.modules.blog.entity;

import com.github.slugify.Slugify;
import com.orthopedic.api.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "blog_posts", indexes = {
        @Index(name = "idx_blog_posts_status_date", columnList = "status, published_at DESC"),
        @Index(name = "idx_blog_posts_slug", columnList = "slug"),
        @Index(name = "idx_blog_posts_category", columnList = "category_id, status"),
        @Index(name = "idx_blog_posts_featured", columnList = "is_featured, status"),
        @Index(name = "idx_blog_posts_author", columnList = "author_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

    public enum BlogPostStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", length = 300, nullable = false)
    private String title;

    @Column(name = "slug", length = 320, nullable = false, unique = true)
    private String slug;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "featured_image_url", length = 500)
    private String featuredImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BlogCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BlogPostStatus status = BlogPostStatus.DRAFT;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "read_time_minutes", nullable = false)
    @Builder.Default
    private Integer readTimeMinutes = 1;

    @Column(name = "meta_title", length = 160)
    private String metaTitle;

    @Column(name = "meta_description", length = 320)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "blog_post_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<BlogTag> tags;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null)
            createdAt = now;
        if (updatedAt == null)
            updatedAt = now;
        if (status == null)
            status = BlogPostStatus.DRAFT;
        if (isFeatured == null)
            isFeatured = false;
        if (viewCount == null)
            viewCount = 0;

        if (slug == null && title != null) {
            slug = Slugify.builder().build().slugify(title);
        }
        if (content != null) {
            int wordCount = content.split("\\s+").length;
            readTimeMinutes = Math.max(1, wordCount / 200);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (content != null) {
            int wordCount = content.split("\\s+").length;
            readTimeMinutes = Math.max(1, wordCount / 200);
        }
    }
}
