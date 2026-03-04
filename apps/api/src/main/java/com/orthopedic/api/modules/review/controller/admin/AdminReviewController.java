package com.orthopedic.api.modules.review.controller.admin;

import com.orthopedic.api.modules.review.dto.response.ReviewResponse;
import com.orthopedic.api.modules.review.service.impl.ReviewServiceImpl;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@Tag(name = "Admin Review Management", description = "Endpoints for administrators to moderate doctor reviews")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
public class AdminReviewController extends BaseController {

    private final ReviewServiceImpl reviewService;

    @GetMapping("/pending")
    @Operation(summary = "List all pending reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getPending(Pageable pageable) {
        return ok(reviewService.getPendingReviews(pageable));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish a review")
    public ResponseEntity<ApiResponse<Void>> publish(@PathVariable UUID id) {
        reviewService.publishReview(id);
        return ok("Review published successfully", null);
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish a review")
    public ResponseEntity<ApiResponse<Void>> unpublish(@PathVariable UUID id) {
        reviewService.unpublishReview(id);
        return ok("Review unpublished successfully", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ok("Review deleted successfully", null);
    }
}
