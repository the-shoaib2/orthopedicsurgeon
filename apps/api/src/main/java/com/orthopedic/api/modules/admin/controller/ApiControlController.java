package com.orthopedic.api.modules.admin.controller;

import com.orthopedic.api.modules.admin.service.ApiControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin/api-controls")
@RequiredArgsConstructor
@Tag(name = "Admin API Controls", description = "Endpoints for managing maintenance mode, blocked IPs, and API circuit breakers")
@PreAuthorize("hasRole('SUPER_ADMIN')") // Super Admin only feature
public class ApiControlController {

    private final ApiControlService apiControlService;

    // --- Maintenance Mode ---

    @GetMapping("/maintenance")
    @Operation(summary = "Get maintenance mode status")
    public ResponseEntity<Map<String, Boolean>> getMaintenanceMode() {
        return ResponseEntity.ok(Map.of("enabled", apiControlService.isMaintenanceModeEnabled()));
    }

    @PostMapping("/maintenance")
    @Operation(summary = "Toggle maintenance mode")
    public ResponseEntity<Map<String, String>> setMaintenanceMode(@RequestBody Map<String, Boolean> body) {
        boolean enabled = Boolean.TRUE.equals(body.get("enabled"));
        apiControlService.setMaintenanceMode(enabled);
        return ResponseEntity.ok(Map.of("message", "Maintenance mode " + (enabled ? "enabled" : "disabled")));
    }

    @GetMapping("/maintenance/allowed-ips")
    @Operation(summary = "Get IPs allowed during maintenance")
    public ResponseEntity<Set<String>> getAllowedIps() {
        return ResponseEntity.ok(apiControlService.getAllowedIps());
    }

    @PostMapping("/maintenance/allowed-ips")
    @Operation(summary = "Add an allowed IP for maintenance mode")
    public ResponseEntity<Map<String, String>> addAllowedIp(@RequestBody Map<String, String> body) {
        String ip = body.get("ip");
        apiControlService.addAllowedIp(ip);
        return ResponseEntity.ok(Map.of("message", "IP " + ip + " added to allowed list"));
    }

    @DeleteMapping("/maintenance/allowed-ips/{ip}")
    @Operation(summary = "Remove an allowed IP")
    public ResponseEntity<Map<String, String>> removeAllowedIp(@PathVariable String ip) {
        apiControlService.removeAllowedIp(ip);
        return ResponseEntity.ok(Map.of("message", "IP " + ip + " removed from allowed list"));
    }

    // --- Blocked IPs ---

    @GetMapping("/blocked-ips")
    @Operation(summary = "Get all blocked IPs")
    public ResponseEntity<Set<String>> getBlockedIps() {
        return ResponseEntity.ok(apiControlService.getBlockedIps());
    }

    @PostMapping("/blocked-ips")
    @Operation(summary = "Block an IP Address")
    public ResponseEntity<Map<String, String>> blockIp(@RequestBody Map<String, String> body) {
        String ip = body.get("ip");
        apiControlService.blockIp(ip);
        return ResponseEntity.ok(Map.of("message", "IP " + ip + " blocked"));
    }

    @DeleteMapping("/blocked-ips/{ip}")
    @Operation(summary = "Unblock an IP Address")
    public ResponseEntity<Map<String, String>> unblockIp(@PathVariable String ip) {
        apiControlService.unblockIp(ip);
        return ResponseEntity.ok(Map.of("message", "IP " + ip + " unblocked"));
    }

    // --- Dynamic API Disabling (Circuit Breaker) ---

    @GetMapping("/disabled-endpoints")
    @Operation(summary = "Get all disabled endpoints")
    public ResponseEntity<Map<String, String>> getDisabledEndpoints() {
        return ResponseEntity.ok(apiControlService.getDisabledApiEndpoints());
    }

    @PostMapping("/disabled-endpoints")
    @Operation(summary = "Disable a specific endpoint")
    public ResponseEntity<Map<String, String>> disableEndpoint(@RequestBody Map<String, String> body) {
        String method = body.get("method");
        String path = body.get("path");
        String reason = body.get("reason");
        apiControlService.disableApiEndpoint(method, path, reason);
        return ResponseEntity.ok(Map.of("message", "Endpoint " + method + " " + path + " disabled"));
    }

    @DeleteMapping("/disabled-endpoints")
    @Operation(summary = "Enable a specific endpoint")
    public ResponseEntity<Map<String, String>> enableEndpoint(@RequestParam String method, @RequestParam String path) {
        apiControlService.enableApiEndpoint(method, path);
        return ResponseEntity.ok(Map.of("message", "Endpoint " + method + " " + path + " enabled"));
    }
}
