package com.orthopedic.api.modules.booking.dto.request;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull(message = "Slot ID is required")
    private UUID slotId;

    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    @NotNull(message = "Chief complaint is required")
    private String chiefComplaint;

    private String notes;

    @Builder.Default
    private Appointment.AppointmentType type = Appointment.AppointmentType.IN_PERSON;
}
