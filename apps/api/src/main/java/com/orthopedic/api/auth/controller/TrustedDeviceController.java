package com.orthopedic.api.auth.controller;

import com.orthopedic.api.auth.dto.TrustedDeviceDto;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.service.TrustedDeviceService;
import com.orthopedic.api.rbac.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/devices")
@RequiredArgsConstructor
@Tag(name = "Trusted Devices", description = "Endpoints for managing trusted devices")
public class TrustedDeviceController {

    private final TrustedDeviceService trustedDeviceService;

    @GetMapping
    @Operation(summary = "Get all trusted devices for current user")
    public ResponseEntity<List<TrustedDeviceDto>> getTrustedDevices(@CurrentUser User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(trustedDeviceService.getTrustedDevices(currentUser));
    }

    @PostMapping
    @Operation(summary = "Register the current device as trusted")
    public ResponseEntity<TrustedDeviceDto> registerDevice(
            @CurrentUser User currentUser,
            @RequestBody RegisterDeviceRequest request,
            HttpServletRequest servletRequest) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        String ipAddress = servletRequest.getRemoteAddr();
        TrustedDeviceDto dto = trustedDeviceService.registerDevice(
                currentUser,
                request.getFingerprint(),
                request.getName(),
                request.getBrowser(),
                request.getOs(),
                ipAddress);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{deviceId}")
    @Operation(summary = "Remove a device from trusted devices list")
    public ResponseEntity<Void> removeDevice(
            @CurrentUser User currentUser,
            @PathVariable UUID deviceId) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        trustedDeviceService.removeDevice(currentUser, deviceId);
        return ResponseEntity.noContent().build();
    }

    @Data
    static class RegisterDeviceRequest {
        private String fingerprint;
        private String name;
        private String browser;
        private String os;
    }
}
