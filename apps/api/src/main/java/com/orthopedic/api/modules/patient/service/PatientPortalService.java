package com.orthopedic.api.modules.patient.service;

import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import com.orthopedic.api.modules.patient.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientDashboardResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.dto.request.UpdatePatientProfileRequest;

import java.util.List;
import java.util.UUID;

public interface PatientPortalService {
    // Group J: Patient Dashboard
    PatientDashboardResponse getDashboard(UUID userId);

    List<AppointmentSummaryResponse> getUpcomingAppointments(UUID userId);

    List<AppointmentSummaryResponse> getPastAppointments(UUID userId);

    // Group K: Health Records
    VitalSignsResponse getLatestVitals(UUID userId);

    List<VitalSignsResponse> getVitalsHistory(UUID userId, int days);

    // Group L: Profile
    PatientResponse getMyProfile(UUID userId);

    PatientResponse updateMyProfile(UUID userId, UpdatePatientProfileRequest request);
}
