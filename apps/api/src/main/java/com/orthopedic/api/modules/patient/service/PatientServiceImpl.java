package com.orthopedic.api.modules.patient.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.patient.dto.request.CreatePatientRequest;
import com.orthopedic.api.modules.patient.dto.request.PatientFilterRequest;
import com.orthopedic.api.modules.patient.dto.response.PatientMedicalHistoryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientSummaryResponse;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.entity.PatientAllergy;
import com.orthopedic.api.modules.patient.entity.PatientMedicalCondition;
import com.orthopedic.api.modules.patient.mapper.PatientMapper;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(PatientRepository patientRepository,
            UserRepository userRepository,
            PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    public PageResponse<PatientSummaryResponse> getAllPatients(PatientFilterRequest filters, Pageable pageable) {
        Page<Patient> patients = patientRepository.findPatients(
                filters.getBloodGroup(),
                filters.getGender(),
                filters.getCity(),
                filters.getStatus(),
                filters.getSearch(),
                pageable);
        return PageResponse.fromPage(patients.map(patientMapper::toSummaryResponse));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR') or @permissionEvaluator.isPatientOwner(authentication, #id)")
    public PatientResponse getPatientById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByUserId(UUID userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for user"));
        return patientMapper.toResponse(patient);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PatientResponse createPatient(CreatePatientRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Patient patient = patientMapper.toEntity(request);
        patient.setUser(user);

        if (request.getAllergies() != null) {
            List<PatientAllergy> allergies = request.getAllergies().stream()
                    .map(patientMapper::toAllergyEntity)
                    .peek(a -> a.setPatient(patient))
                    .collect(Collectors.toList());
            patient.setAllergies(allergies);
        }

        if (request.getConditions() != null) {
            List<PatientMedicalCondition> conditions = request.getConditions().stream()
                    .map(patientMapper::toConditionEntity)
                    .peek(c -> c.setPatient(patient))
                    .collect(Collectors.toList());
            patient.setConditions(conditions);
        }

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or @permissionEvaluator.isPatientOwner(authentication, #id)")
    public PatientMedicalHistoryResponse getMedicalHistory(UUID id) {
        PatientResponse patient = getPatientById(id);

        PatientMedicalHistoryResponse history = new PatientMedicalHistoryResponse();
        history.setPatient(patient);

        // Placeholders for aggregated data - will be populated when modules are
        // integrated
        history.setAppointments(new ArrayList<>());
        history.setPrescriptions(new ArrayList<>());
        history.setLabReports(new ArrayList<>());

        return history;
    }
}
