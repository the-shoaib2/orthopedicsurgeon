package com.orthopedic.api.shared.service;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledEmailService {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    // Run every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        log.info("Starting scheduled appointment reminders...");
        
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Fetch appointments for tomorrow
        List<Appointment> appointments = appointmentRepository.findAllByAppointmentDateAndStatus(
            tomorrow, 
            Appointment.AppointmentStatus.CONFIRMED
        );

        for (Appointment appointment : appointments) {
            try {
                Map<String, Object> vars = new HashMap<>();
                vars.put("patientName", appointment.getPatient().getFirstName());
                vars.put("doctorName", appointment.getDoctor().getUser().getFirstName());
                vars.put("date", appointment.getAppointmentDate().toString());
                vars.put("time", appointment.getStartTime().toString());
                vars.put("hospitalName", appointment.getHospital().getName());

                emailService.sendHtmlEmail(
                    appointment.getPatient().getUser().getEmail(),
                    "Appointment Reminder - Tomorrow",
                    "appointment-reminder.html",
                    vars
                );
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment ID: {}", appointment.getId(), e);
            }
        }
    }
}
