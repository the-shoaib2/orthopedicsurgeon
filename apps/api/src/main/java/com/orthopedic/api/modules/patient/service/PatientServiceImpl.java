package com.orthopedic.api.modules.patient.service;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.patient.dto.request.AdminUpdatePatientRequest;
import com.orthopedic.api.modules.patient.dto.request.CreatePatientRequest;
import com.orthopedic.api.modules.patient.dto.request.PatientFilterRequest;
import com.orthopedic.api.modules.patient.dto.request.UpdatePatientProfileRequest;
import com.orthopedic.api.modules.patient.dto.response.PatientMedicalHistoryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientSummaryResponse;
import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.entity.PatientAllergy;
import com.orthopedic.api.modules.patient.entity.PatientMedicalCondition;
import com.orthopedic.api.modules.patient.mapper.PatientMapper;
import com.orthopedic.api.modules.patient.repository.PatientRepository;
import com.orthopedic.api.modules.audit.annotation.LogMutation;
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

    private Patient getOrCreatePatient(UUID userId) {
        return patientRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Patient newPatient = new Patient();
                    newPatient.setUser(user);
                    newPatient.setDateOfBirth(java.time.LocalDate.of(1900, 1, 1));
                    newPatient.setGender(Patient.Gender.OTHER);
                    newPatient.setStatus(Patient.PatientStatus.ACTIVE);
                    return patientRepository.save(newPatient);
                });
    }

    @Override
    @Transactional
    public PatientResponse getPatientByUserId(UUID userId) {
        Patient patient = getOrCreatePatient(userId);
        return patientMapper.toResponse(patient);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @LogMutation(action = "CREATE_PATIENT", entityName = "PATIENT")
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
    @LogMutation(action = "UPDATE_PATIENT_PROFILE", entityName = "PATIENT")
    public PatientResponse updateMyProfile(UUID userId, UpdatePatientProfileRequest request) {
        Patient patient = getOrCreatePatient(userId);

        User user = patient.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        patient.setBloodGroup(request.getBloodGroup());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setAddress(request.getAddress());
        patient.setCity(request.getCity());

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @LogMutation(action = "ADMIN_UPDATE_PATIENT", entityName = "PATIENT")
    public PatientResponse updatePatient(UUID id, AdminUpdatePatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        User user = patient.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        patient.setBloodGroup(request.getBloodGroup());
        patient.setGender(request.getGender());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setAddress(request.getAddress());
        patient.setCity(request.getCity());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setStatus(request.getStatus());

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @LogMutation(action = "DELETE_PATIENT", entityName = "PATIENT")
    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        patient.setStatus(Patient.PatientStatus.INACTIVE);
        patientRepository.save(patient);
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
