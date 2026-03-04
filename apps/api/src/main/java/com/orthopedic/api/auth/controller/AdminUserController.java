package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.request.UserFilterRequest;
import com.orthopedic.api.auth.dto.response.UserDetailResponse;
import com.orthopedic.api.auth.dto.response.UserSummaryResponse;
import com.orthopedic.api.auth.service.AdminUserService;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "Admin User Management", description = "Endpoints for administrators to manage application users")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminUserController extends BaseController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List all users with filtering and pagination")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getAllUsers(
            UserFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Arrays.asList("email", "firstName", "lastName", "createdAt"));

        return ok(adminUserService.getAllUsers(filters, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable UUID id) {
        return ok(adminUserService.getUserById(id));
    }

    @PostMapping("/{id}/lock")
    @Operation(summary = "Lock a user account")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable UUID id, @RequestBody Map<String, Integer> body) {
        int minutes = body.getOrDefault("minutes", 60);
        adminUserService.lockUser(id, minutes);
        return ok("User locked successfully", null);
    }

    @PostMapping("/{id}/unlock")
    @Operation(summary = "Unlock a user account")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable UUID id) {
        adminUserService.unlockUser(id);
        return ok("User unlocked successfully", null);
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset user password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("New password is required", null));
        }
        adminUserService.resetPassword(id, newPassword);
        return ok("Password reset successfully", null);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user enabled/disabled status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Enabled status is required", null));
        }
        adminUserService.updateUserEnabledStatus(id, enabled);
        return ok("User status updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        adminUserService.deleteUser(id);
        return ok("User deleted successfully", null);
    }
}
