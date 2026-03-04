package com.orthopedic.api.modules.website.controller;

import com.orthopedic.api.modules.website.dto.request.UpdateSiteSettingRequest;
import com.orthopedic.api.modules.website.dto.response.SiteSettingResponse;
import com.orthopedic.api.modules.website.service.SiteSettingService;
import com.orthopedic.api.shared.base.BaseController;
import com.orthopedic.api.shared.dto.ApiResponse;
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
@RequestMapping("/api/v1/admin/site-settings")
@RequiredArgsConstructor
@Tag(name = "Admin Site Settings", description = "Admin endpoints for managing website configurations")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminSiteSettingController extends BaseController {

    private final SiteSettingService siteSettingService;

    @GetMapping
    @Operation(summary = "Get all site settings")
    public ResponseEntity<ApiResponse<List<SiteSettingResponse>>> getAll() {
        return ok(siteSettingService.getAllSettings());
    }

    @GetMapping("/{key}")
    @Operation(summary = "Get site setting by key")
    public ResponseEntity<ApiResponse<SiteSettingResponse>> getByKey(@PathVariable String key) {
        return ok(siteSettingService.getSettingByKey(key));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update site setting by ID")
    public ResponseEntity<ApiResponse<SiteSettingResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateSiteSettingRequest request) {
        return ok(siteSettingService.updateSetting(id, request));
    }

    @PutMapping("/key/{key}")
    @Operation(summary = "Update site setting by key")
    public ResponseEntity<ApiResponse<SiteSettingResponse>> updateByKey(@PathVariable String key,
            @Valid @RequestBody UpdateSiteSettingRequest request) {
        return ok(siteSettingService.updateSettingByKey(key, request));
    }
}
