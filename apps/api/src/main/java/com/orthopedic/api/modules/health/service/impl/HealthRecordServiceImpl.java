package com.orthopedic.api.modules.health.service.impl;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.audit.annotation.LogMutation;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.appointment.mapper.AppointmentMapper;
import com.orthopedic.api.modules.health.dto.request.RecordVitalsRequest;
import com.orthopedic.api.modules.health.dto.response.PatientDashboardResponse;
import com.orthopedic.api.modules.health.dto.response.VitalSignsResponse;
import com.orthopedic.api.modules.health.entity.PatientTimeline;
import com.orthopedic.api.modules.health.entity.VitalSigns;
import com.orthopedic.api.modules.health.repository.MedicalDocumentRepository;
import com.orthopedic.api.modules.health.repository.PatientTimelineRepository;
import com.orthopedic.api.modules.health.repository.TreatmentHistoryRepository;
import com.orthopedic.api.modules.health.repository.VitalSignsRepository;
import com.orthopedic.api.modules.lab.entity.LabReport;
import com.orthopedic.api.modules.lab.repository.LabReportRepository;
import com.orthopedic.api.modules.lab.mapper.LabReportMapper;
import com.orthopedic.api.modules.notification.entity.Notification;
import com.orthopedic.api.modules.notification.repository.NotificationRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.payment.entity.Payment;
import com.orthopedic.api.modules.payment.repository.PaymentRepository;
import com.orthopedic.api.modules.prescription.entity.Prescription;
import com.orthopedic.api.modules.prescription.repository.PrescriptionRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl {

        private final VitalSignsRepository vitalSignsRepository;
        private final PatientRepository patientRepository;
        private final AppointmentRepository appointmentRepository;
        private final MedicalDocumentRepository medicalDocumentRepository;
        private final TreatmentHistoryRepository treatmentHistoryRepository;
        private final PrescriptionRepository prescriptionRepository;
        private final PaymentRepository paymentRepository;
        private final NotificationRepository notificationRepository;
        private final LabReportRepository labReportRepository;
        private final UserRepository userRepository;
        private final PatientTimelineRepository patientTimelineRepository;

        // Mappers
        private final AppointmentMapper appointmentMapper;
        private final LabReportMapper labReportMapper;

        @Transactional
        @LogMutation(action = "RECORD_VITALS", entityName = "VitalSigns")
        public VitalSignsResponse recordVitals(UUID patientId, RecordVitalsRequest request, UUID recordedById) {
                Patient patient = patientRepository.findById(patientId)
                                .orElseThrow(() -> new BusinessException("Patient not found"));

                User recordedBy = userRepository.findById(recordedById)
                                .orElseThrow(() -> new BusinessException("User not found"));

                VitalSigns vitals = VitalSigns.builder()
                                .patient(patient)
                                .recordedBy(recordedBy)
                                .appointment(request.appointmentId() != null
                                                ? appointmentRepository.getReferenceById(request.appointmentId())
                                                : null)
                                .bloodPressureSystolic(request.bloodPressureSystolic())
                                .bloodPressureDiastolic(request.bloodPressureDiastolic())
                                .heartRate(request.heartRate())
                                .temperature(request.temperature())
                                .weight(request.weight())
                                .height(request.height())
                                .oxygenSaturation(request.oxygenSaturation())
                                .notes(request.notes())
                                .build();

                VitalSigns saved = vitalSignsRepository.save(vitals);
                return mapToVitalResponse(saved);
        }

        @Transactional(readOnly = true)
        public PatientDashboardResponse getPatientDashboard(UUID patientId) {
                // Fetch upcoming appointments
                Pageable firstThree = PageRequest.of(0, 3,
                                Sort.by("appointmentDate").ascending().and(Sort.by("startTime").ascending()));
                List<Appointment> upcoming = appointmentRepository.findAppointments(
                                null, patientId, null, Appointment.AppointmentStatus.CONFIRMED, null,
                                LocalDate.now(), null, firstThree).getContent();

                // Counts
                long activePrescriptions = prescriptionRepository.findAllByPatientId(patientId, PageRequest.of(0, 1))
                                .getTotalElements();

                long pendingPayments = paymentRepository.findAllByPatientId(patientId, Pageable.unpaged())
                                .stream().filter(p -> p.getStatus() == Payment.PaymentStatus.PENDING).count();

                long unreadNotifications = notificationRepository.countByRecipientIdAndStatus(
                                patientRepository.findById(patientId)
                                                .orElseThrow(() -> new BusinessException("Patient not found")).getUser()
                                                .getId(),
                                Notification.NotificationStatus.UNREAD);

                // Last visit & Next visit
                Appointment lastVisit = appointmentRepository.findAppointments(
                                null, patientId, null, Appointment.AppointmentStatus.COMPLETED, null,
                                null, LocalDate.now(), PageRequest.of(0, 1, Sort.by("appointmentDate").descending()))
                                .getContent().stream().findFirst().orElse(null);

                Appointment nextVisit = upcoming.stream().findFirst().orElse(null);

                // Recent Lab Reports
                List<LabReport> recentLabs = labReportRepository
                                .findAllByPatientId(patientId, PageRequest.of(0, 5, Sort.by("createdAt").descending()))
                                .getContent();

                // Latest Vitals
                VitalSigns latest = vitalSignsRepository.findFirstByPatientIdOrderByRecordedAtDesc(patientId)
                                .orElse(null);

                return PatientDashboardResponse.builder()
                                .upcomingAppointments(upcoming.stream().map(appointmentMapper::toResponse)
                                                .collect(Collectors.toList()))
                                .activePrescriptions(activePrescriptions)
                                .pendingPayments(pendingPayments)
                                .unreadNotifications(unreadNotifications)
                                .lastVisitDate(lastVisit != null ? lastVisit.getAppointmentDate() : null)
                                .nextAppointment(nextVisit != null ? appointmentMapper.toResponse(nextVisit) : null)
                                .recentLabReports(recentLabs.stream().map(labReportMapper::toResponse)
                                                .collect(Collectors.toList()))
                                .latestVitals(latest != null ? mapToVitalResponse(latest) : null)
                                .build();
        }

        @Transactional(readOnly = true)
        public PageResponse<VitalSignsResponse> getVitalHistory(UUID patientId, Pageable pageable) {
                return PageResponse
                                .fromPage(vitalSignsRepository.findByPatientIdOrderByRecordedAtDesc(patientId, pageable)
                                                .map(this::mapToVitalResponse));
        }

        @Transactional(readOnly = true)
        public PageResponse<PatientTimeline> getPatientTimeline(UUID patientId, Pageable pageable) {
                return PageResponse.fromPage(
                                patientTimelineRepository.findByPatientIdOrderByEventDateDesc(patientId, pageable));
        }

        private VitalSignsResponse mapToVitalResponse(VitalSigns vitals) {
                return VitalSignsResponse.builder()
                                .id(vitals.getId())
                                .systolic(vitals.getBloodPressureSystolic())
                                .diastolic(vitals.getBloodPressureDiastolic())
                                .heartRate(vitals.getHeartRate())
                                .temperature(vitals.getTemperature())
                                .weight(vitals.getWeight())
                                .height(vitals.getHeight())
                                .bmi(vitals.getBmi())
                                .oxygenSaturation(vitals.getOxygenSaturation())
                                .notes(vitals.getNotes())
                                .recordedAt(vitals.getRecordedAt())
                                .build();
        }
}
