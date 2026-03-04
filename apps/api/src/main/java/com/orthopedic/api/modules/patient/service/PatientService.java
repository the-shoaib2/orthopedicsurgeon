package com.orthopedic.api.modules.patient.service;

import com.orthopedic.api.modules.patient.dto.request.AdminUpdatePatientRequest;
import com.orthopedic.api.modules.patient.dto.request.CreatePatientRequest;
import com.orthopedic.api.modules.patient.dto.request.PatientFilterRequest;
import com.orthopedic.api.modules.patient.dto.request.UpdatePatientProfileRequest;
import com.orthopedic.api.modules.patient.dto.response.PatientMedicalHistoryResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientResponse;
import com.orthopedic.api.modules.patient.dto.response.PatientSummaryResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PatientService {
    PageResponse<PatientSummaryResponse> getAllPatients(PatientFilterRequest filters, Pageable pageable);

    PatientResponse getPatientById(UUID id);

    PatientResponse getPatientByUserId(UUID userId);

    PatientResponse createPatient(CreatePatientRequest request);

    PatientResponse updateMyProfile(UUID userId, UpdatePatientProfileRequest request);

    PatientResponse updatePatient(UUID id, AdminUpdatePatientRequest request);

    void deletePatient(UUID id);

    PatientMedicalHistoryResponse getMedicalHistory(UUID id);
}
