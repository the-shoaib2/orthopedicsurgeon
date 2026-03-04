package com.orthopedic.api.modules.patient.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.patient.dto.request.CreatePatientRequest;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.mapper.PatientMapper;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private UUID patientId;
    private User user;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        user = new User();
        user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        patient = new Patient();
        patient.setId(patientId);
        patient.setUser(user);
    }

    @Test
    void getPatientById_Success() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(new PatientResponse());

        PatientResponse response = patientService.getPatientById(patientId);

        assertNotNull(response);
        verify(patientRepository).findById(patientId);
    }

    @Test
    void getPatientByUserId_Success() {
        when(patientRepository.findByUserId(user.getId())).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(new PatientResponse());

        PatientResponse response = patientService.getPatientByUserId(user.getId());

        assertNotNull(response);
    }

    @Test
    void createPatient_Success() {
        CreatePatientRequest request = new CreatePatientRequest();
        request.setUserId(user.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(patientMapper.toEntity(any())).thenReturn(patient);
        when(patientRepository.save(any())).thenReturn(patient);
        when(patientMapper.toResponse(any())).thenReturn(new PatientResponse());

        PatientResponse response = patientService.createPatient(request);

        assertNotNull(response);
        verify(patientRepository).save(any());
    }
}
