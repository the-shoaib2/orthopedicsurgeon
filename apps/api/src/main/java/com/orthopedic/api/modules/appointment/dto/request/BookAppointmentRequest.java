package com.orthopedic.api.modules.appointment.dto.request;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for booking a new appointment")
public class BookAppointmentRequest {
    @NotNull(message = "Doctor ID is required")
    @Schema(description = "Unique identifier of the doctor", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID doctorId;

    @NotNull(message = "Service ID is required")
    @Schema(description = "Unique identifier of the medical service", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID serviceId;

    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    @Schema(description = "Planned date for the appointment", example = "2024-12-25")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required")
    @Schema(description = "Planned start time", example = "10:30")
    private LocalTime startTime;

    @NotNull(message = "Appointment type is required")
    @Schema(description = "Type of appointment", example = "ONLINE")
    private Appointment.AppointmentType type;

    @NotBlank(message = "Chief complaint is required")
    @Schema(description = "Reason for the visit", example = "Severe knee pain since yesterday")
    private String chiefComplaint;

    @Schema(description = "Unique identifier of the patient (Required if booked by staff/admin on behalf of a patient)", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID patientId;
}
