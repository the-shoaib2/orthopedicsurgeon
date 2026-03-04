package com.orthopedic.api.modules.review.controller;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.review.dto.request.CreateReviewRequest;
import com.orthopedic.api.modules.review.dto.response.ReviewResponse;
import com.orthopedic.api.modules.review.service.impl.ReviewServiceImpl;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Endpoints for doctor reviews")
public class ReviewController extends BaseController {

    private final ReviewServiceImpl reviewService;
    private final PatientRepository patientRepository;

    @PostMapping("/patient/reviews")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Create a doctor review")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @CurrentUser User currentUser) {
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));
        return ok("Review submitted. It will be visible after moderation.",
                reviewService.createReview(patient.getId(), request));
    }

    @GetMapping("/patient/reviews")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get my reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @CurrentUser User currentUser,
            Pageable pageable) {
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException("Patient profile not found"));
        return ok(reviewService.getPatientReviews(patient.getId(), pageable));
    }

    @GetMapping("/public/doctors/{doctorId}/reviews")
    @Operation(summary = "Get reviews for a doctor")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getDoctorReviews(@PathVariable UUID doctorId,
            Pageable pageable) {
        return ok(reviewService.getDoctorReviews(doctorId, pageable));
    }
}
