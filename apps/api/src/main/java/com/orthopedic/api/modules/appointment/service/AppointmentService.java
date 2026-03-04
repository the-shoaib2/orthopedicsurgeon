package com.orthopedic.api.modules.appointment.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.dto.request.AppointmentFilterRequest;
import com.orthopedic.api.modules.appointment.dto.request.BookAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.request.RescheduleAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentStatsResponse;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AppointmentService {
    AppointmentResponse bookAppointment(BookAppointmentRequest request, User currentUser);

    AppointmentResponse getAppointmentById(UUID id, User currentUser);

    PageResponse<AppointmentSummaryResponse> getAppointments(AppointmentFilterRequest filters, Pageable pageable,
            User currentUser);

    AppointmentResponse confirmAppointment(UUID id);

    AppointmentResponse startAppointment(UUID id);

    AppointmentResponse completeAppointment(UUID id);

    AppointmentResponse cancelAppointment(UUID id, String reason, User currentUser);

    AppointmentResponse rescheduleAppointment(UUID id, RescheduleAppointmentRequest request, User currentUser);

    // Stats and Calendar views
    AppointmentStatsResponse getStats(User currentUser);

    void bulkCancelAppointments(UUID doctorId, java.time.LocalDate date, String reason, User currentUser);
}
