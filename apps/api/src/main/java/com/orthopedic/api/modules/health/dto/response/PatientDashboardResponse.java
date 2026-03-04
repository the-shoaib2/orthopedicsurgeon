package com.orthopedic.api.modules.health.dto.response;

import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.lab.dto.response.LabReportResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDashboardResponse {
    private List<AppointmentResponse> upcomingAppointments;
    private long activePrescriptions;
    private long pendingPayments;
    private long unreadNotifications;
    private LocalDate lastVisitDate;
    private AppointmentResponse nextAppointment;
    private List<LabReportResponse> recentLabReports;
    private VitalSignsResponse latestVitals;
}
