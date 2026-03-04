package com.orthopedic.api.modules.admin.controller;

import com.orthopedic.api.modules.admin.dto.UserDto;
import com.orthopedic.api.modules.admin.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Endpoints for managing users and roles")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @Operation(summary = "List users with pagination and search")
    public ResponseEntity<Page<UserDto>> getUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(userManagementService.getUsers(pageable, search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userManagementService.getUserById(id));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')") // Only Super Admin can change roles
    @Operation(summary = "Update user roles (Super Admin only)")
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable UUID id, @RequestBody UpdateRolesRequest request) {
        return ResponseEntity.ok(userManagementService.updateUserRoles(id, request.getRoles()));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<UserDto> updateUserStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> request) {
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userManagementService.updateUserStatus(id, enabled));
    }

    @PostMapping("/{id}/unlock")
    @Operation(summary = "Manually unlock a user account")
    public ResponseEntity<UserDto> unlockUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userManagementService.unlockUser(id));
    }

    @Data
    static class UpdateRolesRequest {
        private Set<String> roles;
    }
}
