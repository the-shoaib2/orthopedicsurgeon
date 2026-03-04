package com.orthopedic.api.modules.booking.service.impl;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.booking.dto.request.BookingRequest;
import com.orthopedic.api.modules.booking.dto.response.BookingResponse;
import com.orthopedic.api.modules.booking.dto.response.SlotResponse;
import com.orthopedic.api.modules.booking.entity.AppointmentSlot;
import com.orthopedic.api.modules.booking.repository.AppointmentSlotRepository;
import com.orthopedic.api.modules.booking.service.BookingService;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import com.orthopedic.api.modules.hospital.repository.ServiceRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final AppointmentSlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public List<SlotResponse> getAvailableSlots(UUID doctorId, LocalDate date) {
        return slotRepository
                .findByDoctorIdAndSlotDateAndIsBookedFalseOrderBySlotTimeAsc(doctorId, date)
                .stream()
                .map(this::toSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SlotResponse> getAvailableSlotsForWeek(UUID doctorId, LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        return slotRepository
                .findByDoctorIdAndSlotDateBetweenAndIsBookedFalseOrderBySlotDateAscSlotTimeAsc(doctorId, startDate,
                        endDate)
                .stream()
                .map(this::toSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse bookAppointment(UUID userId, BookingRequest request) {
        AppointmentSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + request.getSlotId()));

        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new IllegalStateException("Slot is already booked");
        }

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found for user: " + userId));

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + request.getServiceId()));

        int durationMinutes = service.getDurationMinutes() != null ? service.getDurationMinutes() : 30;

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(slot.getDoctor());
        appointment.setService(service);
        appointment.setHospital(slot.getDoctor().getHospital());
        appointment.setAppointmentDate(slot.getSlotDate());
        appointment.setStartTime(slot.getSlotTime());
        appointment.setEndTime(slot.getSlotTime().plusMinutes(durationMinutes));
        appointment.setChiefComplaint(request.getChiefComplaint());
        appointment.setNotes(request.getNotes());
        appointment.setType(request.getType() != null ? request.getType() : Appointment.AppointmentType.IN_PERSON);
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        slot.setIsBooked(true);
        slot.setAppointment(saved);
        slotRepository.save(slot);

        return toBookingResponse(saved);
    }

    @Override
    public BookingResponse getBooking(UUID appointmentId, UUID userId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));
        return toBookingResponse(appt);
    }

    @Override
    public List<BookingResponse> getMyAppointments(UUID userId, String status) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found for user: " + userId));

        Appointment.AppointmentStatus statusEnum = null;
        if (status != null) {
            statusEnum = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
        }

        return appointmentRepository.findAppointments(
                null, patient.getId(), null, statusEnum, null, null, null,
                org.springframework.data.domain.PageRequest.of(0, 100)).getContent().stream()
                .map(this::toBookingResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(UUID appointmentId, UUID userId, String reason) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        appt.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appt.setCancellationReason(reason);
        appointmentRepository.save(appt);

        // Free up the slot
        slotRepository.findById(appointmentId).ifPresent(slot -> {
            slot.setIsBooked(false);
            slot.setAppointment(null);
            slotRepository.save(slot);
        });
    }

    @Override
    @Transactional
    public void rescheduleBooking(UUID appointmentId, UUID userId, UUID newSlotId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        AppointmentSlot newSlot = slotRepository.findById(newSlotId)
                .orElseThrow(() -> new IllegalArgumentException("New slot not found: " + newSlotId));

        if (Boolean.TRUE.equals(newSlot.getIsBooked())) {
            throw new IllegalStateException("New slot is already booked");
        }

        // Free old slot
        slotRepository.findAll().stream()
                .filter(s -> appt.equals(s.getAppointment()))
                .findFirst()
                .ifPresent(oldSlot -> {
                    oldSlot.setIsBooked(false);
                    oldSlot.setAppointment(null);
                    slotRepository.save(oldSlot);
                });

        // Update appointment with new slot details
        appt.setAppointmentDate(newSlot.getSlotDate());
        appt.setStartTime(newSlot.getSlotTime());
        appt.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
        appointmentRepository.save(appt);

        // Mark new slot as booked
        newSlot.setIsBooked(true);
        newSlot.setAppointment(appt);
        slotRepository.save(newSlot);
    }

    private SlotResponse toSlotResponse(AppointmentSlot slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .doctorId(slot.getDoctor() != null ? slot.getDoctor().getId() : null)
                .date(slot.getSlotDate())
                .time(slot.getSlotTime())
                .isBooked(Boolean.TRUE.equals(slot.getIsBooked()))
                .build();
    }

    private BookingResponse toBookingResponse(Appointment appt) {
        String doctorName = appt.getDoctor() != null && appt.getDoctor().getUser() != null
                ? appt.getDoctor().getUser().getFirstName() + " " + appt.getDoctor().getUser().getLastName()
                : "Unknown";
        String serviceName = appt.getService() != null ? appt.getService().getName() : null;

        return BookingResponse.builder()
                .appointmentId(appt.getId())
                .doctorName(doctorName)
                .serviceName(serviceName)
                .date(appt.getAppointmentDate())
                .startTime(appt.getStartTime())
                .status(appt.getStatus())
                .build();
    }
}
