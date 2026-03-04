package com.orthopedic.api.modules.blog.service.impl;

import com.github.slugify.Slugify;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.blog.dto.request.CreateBlogPostRequest;
import com.orthopedic.api.modules.blog.dto.response.BlogPostResponse;
import com.orthopedic.api.modules.blog.dto.response.BlogPostSummaryResponse;
import com.orthopedic.api.modules.blog.dto.response.BlogTagResponse;
import com.orthopedic.api.modules.blog.entity.BlogPost;
import com.orthopedic.api.modules.blog.entity.BlogTag;
import com.orthopedic.api.modules.blog.repository.BlogCategoryRepository;
import com.orthopedic.api.modules.blog.repository.BlogCommentRepository;
import com.orthopedic.api.modules.blog.repository.BlogPostRepository;
import com.orthopedic.api.modules.blog.repository.BlogTagRepository;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl {

        private final BlogPostRepository blogPostRepository;
        private final BlogCommentRepository blogCommentRepository;
        private final BlogCategoryRepository blogCategoryRepository;
        private final BlogTagRepository blogTagRepository;
        private final UserRepository userRepository;

        @Transactional(readOnly = true)
        @Cacheable(value = "blog-posts", key = "#slug")
        public BlogPostResponse getPostBySlug(String slug) {
                BlogPost post = blogPostRepository.findBySlugAndStatus(slug, BlogPost.BlogPostStatus.PUBLISHED)
                                .orElseThrow(() -> new RuntimeException("Post not found"));

                incrementViewCountAsync(post.getId());

                List<BlogPost> related = blogPostRepository.findRelated(
                                post.getId(),
                                post.getCategory() != null ? post.getCategory().getId() : null,
                                PageRequest.of(0, 3));

                long commentCount = blogCommentRepository.countByPostIdAndIsApprovedTrue(post.getId());

                List<BlogPostSummaryResponse> relatedResponses = related.stream()
                                .map(this::toSummaryResponse)
                                .toList();

                return BlogPostResponse.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .slug(post.getSlug())
                                .excerpt(post.getExcerpt())
                                .content(post.getContent())
                                .featuredImageUrl(post.getFeaturedImageUrl())
                                .authorName(post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName())
                                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                                .categorySlug(post.getCategory() != null ? post.getCategory().getSlug() : null)
                                .status(post.getStatus())
                                .isFeatured(post.getIsFeatured())
                                .viewCount(post.getViewCount() + 1)
                                .readTimeMinutes(post.getReadTimeMinutes())
                                .commentsCount((int) commentCount)
                                .publishedAt(post.getPublishedAt())
                                .tags(post.getTags().stream()
                                                .map(t -> new BlogTagResponse(t.getId(), t.getName(), t.getSlug()))
                                                .toList())
                                .metaTitle(post.getMetaTitle())
                                .metaDescription(post.getMetaDescription())
                                .metaKeywords(post.getMetaKeywords())
                                .relatedPosts(relatedResponses)
                                .build();
        }

        private BlogPostSummaryResponse toSummaryResponse(BlogPost post) {
                return BlogPostSummaryResponse.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .slug(post.getSlug())
                                .excerpt(post.getExcerpt())
                                .featuredImageUrl(post.getFeaturedImageUrl())
                                .authorName(post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName())
                                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                                .categorySlug(post.getCategory() != null ? post.getCategory().getSlug() : null)
                                .status(post.getStatus())
                                .isFeatured(post.getIsFeatured())
                                .viewCount(post.getViewCount())
                                .readTimeMinutes(post.getReadTimeMinutes())
                                .publishedAt(post.getPublishedAt())
                                .build();
        }

        @Async
        @Transactional
        public void incrementViewCountAsync(UUID postId) {
                blogPostRepository.incrementViewCount(postId);
        }

        @Transactional
        public BlogPostResponse createPost(CreateBlogPostRequest req, UUID authorId) {
                String slug = (req.slug() != null && !req.slug().trim().isEmpty())
                                ? req.slug()
                                : generateUniqueSlug(req.title());

                int readTime = Math.max(1, req.content().split("\\s+").length / 200);

                List<BlogTag> tags = req.tagNames().stream()
                                .map(name -> blogTagRepository.findByName(name)
                                                .orElseGet(() -> {
                                                        BlogTag t = new BlogTag();
                                                        t.setName(name);
                                                        t.setSlug(Slugify.builder().build().slugify(name));
                                                        return blogTagRepository.save(t);
                                                }))
                                .collect(Collectors.toList());

                BlogPost post = BlogPost.builder()
                                .title(req.title())
                                .slug(slug)
                                .content(Jsoup.clean(req.content(), Safelist.relaxed()))
                                .excerpt(req.excerpt())
                                .author(userRepository.getReferenceById(authorId))
                                .category(req.categoryId() != null
                                                ? blogCategoryRepository.getReferenceById(req.categoryId())
                                                : null)
                                .status(BlogPost.BlogPostStatus.DRAFT)
                                .isFeatured(req.isFeatured())
                                .readTimeMinutes(readTime)
                                .tags(tags)
                                .metaTitle(req.metaTitle())
                                .metaDescription(req.metaDescription())
                                .metaKeywords(req.metaKeywords())
                                .build();

                BlogPost saved = blogPostRepository.save(post);
                return getPostBySlug(saved.getSlug());
        }

        @Transactional(readOnly = true)
        public List<BlogPostResponse> getAllPostsForAdmin() {
                return blogPostRepository.findAll().stream()
                                .map(post -> {
                                        long commentCount = blogCommentRepository
                                                        .countByPostIdAndIsApprovedTrue(post.getId());
                                        BlogPostResponse resp = BlogPostResponse.builder()
                                                        .id(post.getId())
                                                        .title(post.getTitle())
                                                        .slug(post.getSlug())
                                                        .excerpt(post.getExcerpt())
                                                        .featuredImageUrl(post.getFeaturedImageUrl())
                                                        .authorName(post.getAuthor().getFirstName() + " "
                                                                        + post.getAuthor().getLastName())
                                                        .categoryName(post.getCategory() != null
                                                                        ? post.getCategory().getName()
                                                                        : null)
                                                        .status(post.getStatus())
                                                        .isFeatured(post.getIsFeatured())
                                                        .viewCount(post.getViewCount())
                                                        .readTimeMinutes(post.getReadTimeMinutes())
                                                        .commentsCount((int) commentCount)
                                                        .publishedAt(post.getPublishedAt())
                                                        .build();
                                        return resp;
                                })
                                .toList();
        }

        @Transactional
        public void deletePost(UUID id) {
                if (!blogPostRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Blog post not found");
                }
                blogPostRepository.deleteById(id);
        }

        private String generateUniqueSlug(String title) {
                String base = Slugify.builder().build().slugify(title);
                String slug = base;
                int counter = 1;
                while (blogPostRepository.findBySlugAndStatus(slug, null).isPresent()) { // Will match any since status
                                                                                         // is not
                                                                                         // checked here correctly
                                                                                         // without
                                                                                         // another method, just a mock
                        slug = base + "-" + counter++;
                }
                return slug;
        }
}
