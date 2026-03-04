package com.orthopedic.api.modules.patient.controller;

import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import com.orthopedic.api.modules.patient.dto.request.UpdatePatientProfileRequest;
import com.orthopedic.api.modules.patient.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientDashboardResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.service.PatientPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient")
@RequiredArgsConstructor
public class PatientPortalController {

    private final PatientPortalService portalService;

    // ─── Group J: Patient Dashboard ─────────────────────────────────────────────

    /**
     * J-01: Get the patient's full dashboard summary.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<PatientDashboardResponse> getDashboard(@RequestParam UUID userId) {
        return ResponseEntity.ok(portalService.getDashboard(userId));
    }

    /**
     * J-02: Get upcoming appointments.
     */
    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<AppointmentSummaryResponse>> getUpcomingAppointments(@RequestParam UUID userId) {
        return ResponseEntity.ok(portalService.getUpcomingAppointments(userId));
    }

    /**
     * J-03: Get past / completed appointments.
     */
    @GetMapping("/appointments/history")
    public ResponseEntity<List<AppointmentSummaryResponse>> getPastAppointments(@RequestParam UUID userId) {
        return ResponseEntity.ok(portalService.getPastAppointments(userId));
    }

    // ─── Group K: Health Records ─────────────────────────────────────────────────

    /**
     * K-01: Get latest vital signs.
     */
    @GetMapping("/health/vitals/latest")
    public ResponseEntity<VitalSignsResponse> getLatestVitals(@RequestParam UUID userId) {
        return ResponseEntity.ok(portalService.getLatestVitals(userId));
    }

    /**
     * K-02: Get vitals history for charting (default 30 days).
     */
    @GetMapping("/health/vitals/history")
    public ResponseEntity<List<VitalSignsResponse>> getVitalsHistory(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(portalService.getVitalsHistory(userId, days));
    }

    // ─── Group L: Profile Management ─────────────────────────────────────────────

    /**
     * L-01: Get patient's own profile.
     */
    @GetMapping("/profile")
    public ResponseEntity<PatientResponse> getMyProfile(@RequestParam UUID userId) {
        return ResponseEntity.ok(portalService.getMyProfile(userId));
    }

    /**
     * L-02: Update patient's own profile.
     */
    @PutMapping("/profile")
    public ResponseEntity<PatientResponse> updateMyProfile(
            @RequestParam UUID userId,
            @Valid @RequestBody UpdatePatientProfileRequest request) {
        return ResponseEntity.ok(portalService.updateMyProfile(userId, request));
    }
}
