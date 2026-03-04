package com.orthopedic.api.modules.audit.controller;

import com.orthopedic.api.modules.audit.dto.response.AuditLogResponse;
import com.orthopedic.api.modules.audit.service.AuditService;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit Management", description = "Endpoints for viewing system mutation logs (Super Admin only)")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AuditController extends BaseController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @Operation(summary = "Get all system audit logs")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageableUtils.createPageable(page, size, sort, direction,
                Arrays.asList("createdAt", "userId", "entityType", "action"));

        return ok(auditService.getAllLogs(pageable));
    }

    @GetMapping("/entity/{name}/{id}")
    @Operation(summary = "Get audit logs for a specific entity instance")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getByEntity(
            @PathVariable String name,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageableUtils.createPageable(page, size, "createdAt", "DESC", null);
        return ok(auditService.getLogsByEntity(name, id, pageable));
    }
}
