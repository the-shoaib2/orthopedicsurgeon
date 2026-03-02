package com.orthopedic.api.modules.doctor.service;

import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.doctor.dto.request.CreateDoctorRequest;
import com.orthopedic.api.modules.doctor.dto.request.DoctorFilterRequest;
import com.orthopedic.api.modules.doctor.dto.response.DoctorResponse;
import com.orthopedic.api.modules.doctor.dto.response.DoctorSummaryResponse;
import com.orthopedic.api.modules.doctor.entity.Doctor;
import com.orthopedic.api.modules.doctor.entity.DoctorAvailability;
import com.orthopedic.api.modules.doctor.mapper.DoctorMapper;
import com.orthopedic.api.modules.doctor.repository.DoctorRepository;
import com.orthopedic.api.modules.hospital.repository.HospitalRepository;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import com.orthopedic.api.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "doctors", key = "#filters.toString() + #pageable.pageNumber")
    public PageResponse<DoctorSummaryResponse> getAllDoctors(DoctorFilterRequest filters, Pageable pageable) {
        Page<Doctor> doctors = doctorRepository.findAvailableDoctors(
                filters.getSpecialization(),
                filters.getHospitalId(),
                filters.getCity(),
                filters.getAvailableForOnline(),
                pageable
        );
        return PageResponse.fromPage(doctors.map(doctorMapper::toSummaryResponse));
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "doctors", key = "#id")
    public DoctorResponse getDoctorById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return mapToResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for user"));
        return mapToResponse(doctor);
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @CacheEvict(value = "doctors", allEntries = true)
    public DoctorResponse createDoctor(CreateDoctorRequest request) {
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessException("License number already exists");
        }
        
        Doctor doctor = doctorMapper.toEntity(request);
        doctor.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        doctor.setHospital(hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found")));
        
        if (request.getAvailabilities() != null) {
            List<DoctorAvailability> availabilities = request.getAvailabilities().stream()
                    .map(doctorMapper::toAvailabilityEntity)
                    .peek(a -> a.setDoctor(doctor))
                    .collect(Collectors.toList());
            doctor.setAvailabilities(availabilities);
        }

        return mapToResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "doctor_slots", key = "#doctorId + #date.toString()")
    public List<LocalTime> getAvailableSlots(UUID doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        // Find availability for the day of week
        DoctorAvailability availability = doctor.getAvailabilities().stream()
                .filter(a -> a.getDayOfWeek() == date.getDayOfWeek() && a.getIsAvailable())
                .findFirst()
                .orElse(null);

        if (availability == null) {
            return new ArrayList<>();
        }

        // Generate slots (e.g., every 30 mins)
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = availability.getStartTime();
        while (current.isBefore(availability.getEndTime())) {
            slots.add(current);
            current = current.plusMinutes(30);
        }

        // Logic to subtract occupied slots will be implemented in Appointment module integration
        return slots;
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        DoctorResponse response = doctorMapper.toResponse(doctor);
        response.setTotalAppointments(doctorRepository.countTotalAppointments(doctor.getId()));
        // response.setAverageRating(...) 
        return response;
    }
}
