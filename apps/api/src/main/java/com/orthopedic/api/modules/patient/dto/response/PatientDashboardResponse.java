package com.orthopedic.api.modules.patient.dto.response;

import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PatientDashboardResponse(
                List<AppointmentSummaryResponse> upcomingAppointments,
                AppointmentSummaryResponse nextAppointment,
                long activePrescriptions,
                long pendingPayments,
                long unreadNotifications,
                LocalDate lastVisitDate,
                VitalSignsResponse latestVitals) {
}
