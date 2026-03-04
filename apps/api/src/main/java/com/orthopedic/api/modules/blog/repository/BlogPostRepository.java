package com.orthopedic.api.modules.blog.repository;

import com.orthopedic.api.modules.blog.entity.BlogPost;
import com.orthopedic.api.modules.blog.entity.BlogPost.BlogPostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    Optional<BlogPost> findBySlugAndStatus(String slug, BlogPostStatus status);

    Page<BlogPost> findByStatus(BlogPostStatus status, Pageable pageable);

    Page<BlogPost> findByIsFeaturedTrueAndStatus(BlogPostStatus status, Pageable pageable);

    Page<BlogPost> findByCategorySlugAndStatus(String categorySlug, BlogPostStatus status, Pageable pageable);

    @Query("SELECT p FROM BlogPost p JOIN p.tags t WHERE t.slug = :tagSlug AND p.status = 'PUBLISHED'")
    Page<BlogPost> findByTagSlug(@Param("tagSlug") String tagSlug, Pageable pageable);

    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' AND p.id != :excludeId AND p.category.id = :categoryId ORDER BY p.viewCount DESC")
    List<BlogPost> findRelated(@Param("excludeId") UUID excludeId, @Param("categoryId") UUID categoryId,
            Pageable pageable);

    @Modifying
    @Query("UPDATE BlogPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    List<BlogPost> findTop5ByStatusOrderByViewCountDesc(BlogPostStatus status);

    @Query("SELECT p FROM BlogPost p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "p.status = 'PUBLISHED'")
    List<BlogPost> searchPosts(@Param("query") String query);
}
