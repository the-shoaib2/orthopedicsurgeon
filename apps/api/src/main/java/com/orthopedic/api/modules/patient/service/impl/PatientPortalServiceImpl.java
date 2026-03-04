package com.orthopedic.api.modules.patient.service.impl;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import com.orthopedic.api.modules.health.entity.VitalSigns;
import com.orthopedic.api.modules.health.repository.VitalSignsRepository;
import com.orthopedic.api.modules.patient.dto.request.UpdatePatientProfileRequest;
import com.orthopedic.api.modules.patient.dto.response.AppointmentSummaryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientDashboardResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.patient.service.PatientPortalService;
import com.orthopedic.api.modules.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientPortalServiceImpl implements PatientPortalService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final VitalSignsRepository vitalSignsRepository;
    private final PatientService patientService;

    @Override
    public PatientDashboardResponse getDashboard(UUID userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + userId));

        List<AppointmentSummaryResponse> upcoming = getUpcomingAppointments(userId);
        AppointmentSummaryResponse next = upcoming.isEmpty() ? null : upcoming.get(0);

        VitalSignsResponse vitals = getLatestVitals(userId);

        LocalDate lastVisit = appointmentRepository
                .findAppointments(null, patient.getId(), null, Appointment.AppointmentStatus.COMPLETED,
                        null, null, null, PageRequest.of(0, 1))
                .getContent().stream().findFirst()
                .map(Appointment::getAppointmentDate)
                .orElse(null);

        return PatientDashboardResponse.builder()
                .upcomingAppointments(upcoming)
                .nextAppointment(next)
                .activePrescriptions(0L) // Future phase
                .pendingPayments(0L) // Future phase
                .unreadNotifications(0L) // Future phase
                .lastVisitDate(lastVisit)
                .latestVitals(vitals)
                .build();
    }

    @Override
    public List<AppointmentSummaryResponse> getUpcomingAppointments(UUID userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + userId));

        return appointmentRepository.findAppointments(
                null, patient.getId(), null, null, null, LocalDate.now(), null,
                PageRequest.of(0, 10)).getContent().stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.PENDING
                        || a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentSummaryResponse> getPastAppointments(UUID userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + userId));

        return appointmentRepository.findAppointments(
                null, patient.getId(), null, Appointment.AppointmentStatus.COMPLETED, null, null, null,
                PageRequest.of(0, 20)).getContent().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public VitalSignsResponse getLatestVitals(UUID userId) {
        Patient patient = patientRepository.findByUserId(userId).orElse(null);
        if (patient == null)
            return null;

        return vitalSignsRepository.findFirstByPatientIdOrderByRecordedAtDesc(patient.getId())
                .map(this::toVitalsResponse)
                .orElse(null);
    }

    @Override
    public List<VitalSignsResponse> getVitalsHistory(UUID userId, int days) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + userId));

        LocalDateTime from = LocalDateTime.now().minusDays(days);
        return vitalSignsRepository.findForChart(patient.getId(), from)
                .stream()
                .map(this::toVitalsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientResponse getMyProfile(UUID userId) {
        return patientService.getPatientByUserId(userId);
    }

    @Override
    @Transactional
    public PatientResponse updateMyProfile(UUID userId, UpdatePatientProfileRequest request) {
        return patientService.updateMyProfile(userId, request);
    }

    private AppointmentSummaryResponse toSummary(Appointment a) {
        String doctorName = a.getDoctor() != null && a.getDoctor().getUser() != null
                ? a.getDoctor().getUser().getFirstName() + " " + a.getDoctor().getUser().getLastName()
                : "Unknown";
        return AppointmentSummaryResponse.builder()
                .id(a.getId())
                .doctorName(doctorName)
                .specialization(a.getDoctor() != null ? a.getDoctor().getSpecialization() : null)
                .serviceName(a.getService() != null ? a.getService().getName() : null)
                .date(a.getAppointmentDate())
                .time(a.getStartTime())
                .status(a.getStatus())
                .type(a.getType())
                .build();
    }

    private VitalSignsResponse toVitalsResponse(VitalSigns v) {
        String recordedBy = v.getRecordedBy() != null
                ? v.getRecordedBy().getFirstName() + " " + v.getRecordedBy().getLastName()
                : null;
        return VitalSignsResponse.builder()
                .id(v.getId())
                .systolic(v.getBloodPressureSystolic())
                .diastolic(v.getBloodPressureDiastolic())
                .heartRate(v.getHeartRate())
                .temperature(v.getTemperature())
                .weight(v.getWeight())
                .height(v.getHeight())
                .bmi(v.getBmi())
                .oxygenSaturation(v.getOxygenSaturation())
                .notes(v.getNotes())
                .recordedAt(v.getRecordedAt())
                .recordedByName(recordedBy)
                .build();
    }
}
