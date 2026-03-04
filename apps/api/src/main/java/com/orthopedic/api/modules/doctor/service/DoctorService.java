package com.orthopedic.api.modules.doctor.service;

import com.orthopedic.api.modules.doctor.dto.request.CreateDoctorRequest;
import com.orthopedic.api.modules.doctor.dto.request.DoctorFilterRequest;
import com.orthopedic.api.modules.doctor.dto.response.DoctorResponse;
import com.orthopedic.api.modules.doctor.dto.response.DoctorSummaryResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface DoctorService {
    PageResponse<DoctorSummaryResponse> getAllDoctors(DoctorFilterRequest filters, Pageable pageable);

    DoctorResponse getDoctorById(UUID id);

    DoctorResponse getDoctorByUserId(UUID userId);

    DoctorResponse createDoctor(CreateDoctorRequest request);

    DoctorResponse updateDoctor(UUID id, CreateDoctorRequest request);

    void deleteDoctor(UUID id);

    List<LocalTime> getAvailableSlots(UUID doctorId, LocalDate date);

    void updateDoctorStatus(UUID id, com.orthopedic.api.modules.doctor.entity.Doctor.DoctorStatus status);

    void toggleFeaturedStatus(UUID id, boolean featured);
}
