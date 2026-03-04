package com.orthopedic.api.modules.appointment.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.appointment.dto.request.BookAppointmentRequest;
import com.orthopedic.api.modules.appointment.dto.response.AppointmentResponse;
import com.orthopedic.api.modules.appointment.entity.Appointment;
import com.orthopedic.api.modules.appointment.mapper.AppointmentMapper;
import com.orthopedic.api.modules.appointment.repository.AppointmentRepository;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.entity.Hospital;
import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import com.orthopedic.api.modules.hospital.repository.ServiceRepository;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.shared.exception.SlotUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private io.micrometer.core.instrument.Counter appointmentBookedCounter;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User currentUser;
    private Doctor doctor;
    private Patient patient;
    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        currentUser.setEmail("patient@test.com");

        doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setHospital(new Hospital());

        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setUser(currentUser);

        service = new ServiceEntity();
        service.setId(UUID.randomUUID());
        service.setDurationMinutes(30);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void bookAppointment_Success() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setDoctorId(doctor.getId());
        request.setServiceId(service.getId());
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(10, 0));

        when(valueOperations.setIfAbsent(anyString(), any(), any())).thenReturn(true);
        when(doctorRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(any(), any(), any(),
                any()))
                .thenReturn(false);

        when(appointmentMapper.toEntity(any())).thenReturn(new Appointment());
        when(appointmentRepository.save(any())).thenReturn(new Appointment());
        when(appointmentMapper.toResponse(any())).thenReturn(new AppointmentResponse());

        AppointmentResponse response = appointmentService.bookAppointment(request, currentUser);

        assertNotNull(response);
        verify(appointmentRepository).save(any());
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void bookAppointment_SlotUnavailable() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setDoctorId(doctor.getId());
        request.setAppointmentDate(LocalDate.now());
        request.setStartTime(LocalTime.of(10, 0));

        when(valueOperations.setIfAbsent(anyString(), any(), any())).thenReturn(true);
        when(doctorRepository.findById(any())).thenReturn(Optional.of(doctor));
        when(serviceRepository.findById(any())).thenReturn(Optional.of(service));
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(patient));
        when(appointmentRepository.existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(any(), any(), any(),
                any()))
                .thenReturn(true);

        assertThrows(SlotUnavailableException.class, () -> appointmentService.bookAppointment(request, currentUser));
    }
}
