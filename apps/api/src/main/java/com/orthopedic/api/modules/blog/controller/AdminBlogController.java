package com.orthopedic.api.modules.blog.controller;

import com.orthopedic.api.modules.blog.dto.request.CreateBlogPostRequest;
import com.orthopedic.api.modules.blog.dto.response.BlogPostResponse;
import com.orthopedic.api.modules.blog.service.impl.BlogServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/blog")
@RequiredArgsConstructor
@Tag(name = "Admin Blog", description = "Admin endpoints for blog management")
public class AdminBlogController {

    private final BlogServiceImpl blogService;

    @GetMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all blog posts for admin")
    public ResponseEntity<List<BlogPostResponse>> getAllPosts() {
        return ResponseEntity.ok(blogService.getAllPostsForAdmin());
    }

    @PostMapping("/posts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new blog post")
    public ResponseEntity<BlogPostResponse> createPost(@Valid @RequestBody CreateBlogPostRequest request) {
        // Placeholder authorId
        UUID authorId = UUID.randomUUID();
        return ResponseEntity.ok(blogService.createPost(request, authorId));
    }

    @DeleteMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a blog post")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        blogService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
