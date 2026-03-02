package com.orthopedic.api.modules.doctor.service;

import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.doctor.dto.request.DoctorFilterRequest;
import com.orthopedic.api.modules.doctor.dto.response.DoctorResponse;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.mapper.DoctorMapper;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.repository.HospitalRepository;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private HospitalRepository hospitalRepository;
    @Mock private UserRepository userRepository;
    @Mock private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setSpecialization("Orthopedics");
    }

    @Test
    void getDoctorById_Success() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toResponse(doctor)).thenReturn(new DoctorResponse());
        when(doctorRepository.countTotalAppointments(doctorId)).thenReturn(10L);

        DoctorResponse response = doctorService.getDoctorById(doctorId);

        assertNotNull(response);
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void getDoctorById_NotFound() {
        when(doctorRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(UUID.randomUUID()));
    }

    @Test
    void getAllDoctors_Success() {
        DoctorFilterRequest filters = new DoctorFilterRequest();
        Pageable pageable = Pageable.unpaged();
        Page<Doctor> page = new PageImpl<>(Collections.singletonList(doctor));

        when(doctorRepository.findAvailableDoctors(any(), any(), any(), any(), any())).thenReturn(page);
        
        doctorService.getAllDoctors(filters, pageable);

        verify(doctorRepository).findAvailableDoctors(any(), any(), any(), any(), any());
    }
}
