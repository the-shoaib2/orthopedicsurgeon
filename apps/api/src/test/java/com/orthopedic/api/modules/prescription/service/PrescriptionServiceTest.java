package com.orthopedic.api.modules.prescription.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.prescription.dto.request.CreatePrescriptionRequest;
import com.orthopedic.api.modules.prescription.dto.response.PrescriptionResponse;
import com.orthopedic.api.modules.prescription.entity.Prescription;
import com.orthopedic.api.modules.prescription.mapper.PrescriptionMapper;
import com.orthopedic.api.modules.prescription.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private User doctorUser;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        doctorUser = new User();
        doctorUser.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);

        appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDoctor(doctor);
        appointment.setPatient(new Patient());
    }

    @Test
    void createPrescription_Success() {
        CreatePrescriptionRequest request = new CreatePrescriptionRequest();
        request.setAppointmentId(appointment.getId());
        request.setMedicines(new ArrayList<>());

        when(appointmentRepository.findById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(prescriptionRepository.findByAppointmentId(any())).thenReturn(Optional.empty());
        when(prescriptionMapper.toEntity(any())).thenReturn(new Prescription());
        when(prescriptionRepository.save(any())).thenReturn(new Prescription());
        when(prescriptionMapper.toResponse(any())).thenReturn(new PrescriptionResponse());

        PrescriptionResponse response = prescriptionService.createPrescription(request, doctorUser);

        assertNotNull(response);
        verify(prescriptionRepository).save(any());
    }
}
