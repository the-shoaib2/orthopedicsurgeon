package com.orthopedic.api.modules.hospital.service;

import com.orthopedic.api.modules.hospital.dto.request.CreateHospitalRequest;
import com.orthopedic.api.modules.hospital.dto.request.CreateServiceRequest;
import com.orthopedic.api.modules.hospital.dto.request.UpdateHospitalRequest;
import com.orthopedic.api.modules.hospital.dto.request.UpdateServiceRequest;
import com.orthopedic.api.modules.hospital.dto.response.HospitalResponse;
import com.orthopedic.api.modules.hospital.dto.response.ServiceResponse;
import com.orthopedic.api.modules.hospital.entity.Hospital;
import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import com.orthopedic.api.modules.hospital.mapper.HospitalMapper;
import com.orthopedic.api.modules.hospital.repository.HospitalRepository;
import com.orthopedic.api.modules.hospital.repository.ServiceRepository;
import com.orthopedic.api.modules.audit.annotation.LogMutation;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import com.orthopedic.api.shared.exception.BusinessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final ServiceRepository serviceRepository;
    private final HospitalMapper hospitalMapper;

    public HospitalServiceImpl(HospitalRepository hospitalRepository,
            ServiceRepository serviceRepository,
            HospitalMapper hospitalMapper) {
        this.hospitalRepository = hospitalRepository;
        this.serviceRepository = serviceRepository;
        this.hospitalMapper = hospitalMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hospitals", key = "#status + #city + #pageable.pageNumber")
    public Page<HospitalResponse> getAllHospitals(Hospital.HospitalStatus status, String city, Pageable pageable) {
        Page<Hospital> hospitals;
        if (city != null) {
            hospitals = hospitalRepository.findAllByCity(city, pageable);
        } else if (status != null) {
            hospitals = hospitalRepository.findAllByStatus(status, pageable);
        } else {
            hospitals = hospitalRepository.findAll(pageable);
        }
        return hospitals.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "hospitals", key = "#id")
    public HospitalResponse getHospitalById(UUID id) {
        return hospitalRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }

    @Override
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @CacheEvict(value = "hospitals", allEntries = true)
    @LogMutation(action = "CREATE_HOSPITAL", entityName = "HOSPITAL")
    public HospitalResponse createHospital(CreateHospitalRequest request) {
        if (hospitalRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BusinessException("License number already exists");
        }
        Hospital hospital = hospitalMapper.toEntity(request);
        return mapToResponse(hospitalRepository.save(hospital));
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @CacheEvict(value = "hospitals", allEntries = true)
    @LogMutation(action = "UPDATE_HOSPITAL", entityName = "HOSPITAL")
    public HospitalResponse updateHospital(UUID id, UpdateHospitalRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        hospitalMapper.updateEntity(request, hospital);
        return mapToResponse(hospitalRepository.save(hospital));
    }

    @Override
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @CacheEvict(value = "hospitals", allEntries = true)
    @LogMutation(action = "DELETE_HOSPITAL", entityName = "HOSPITAL")
    public void deleteHospital(UUID id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        hospital.setStatus(Hospital.HospitalStatus.INACTIVE);
        hospitalRepository.save(hospital);
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "hospital_services", key = "#hospitalId")
    public List<ServiceResponse> getServicesByHospital(UUID hospitalId) {
        return serviceRepository.findAllByHospitalId(hospitalId).stream()
                .map(hospitalMapper::toServiceResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @LogMutation(action = "CREATE_SERVICE", entityName = "SERVICE")
    public ServiceResponse createService(CreateServiceRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        ServiceEntity service = hospitalMapper.toServiceEntity(request);
        service.setHospital(hospital);
        return hospitalMapper.toServiceResponse(serviceRepository.save(service));
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @LogMutation(action = "UPDATE_SERVICE", entityName = "SERVICE")
    public ServiceResponse updateService(UUID serviceId, UpdateServiceRequest request) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        hospitalMapper.updateServiceEntity(request, service);
        return hospitalMapper.toServiceResponse(serviceRepository.save(service));
    }

    @Override
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @LogMutation(action = "DELETE_SERVICE", entityName = "SERVICE")
    public void deleteService(UUID serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        service.setStatus(ServiceEntity.ServiceStatus.INACTIVE);
        serviceRepository.save(service);
    }

    private HospitalResponse mapToResponse(Hospital hospital) {
        HospitalResponse response = hospitalMapper.toResponse(hospital);
        response.setDoctorCount(hospitalRepository.countDoctors(hospital.getId()));
        response.setServiceCount(hospitalRepository.countServices(hospital.getId()));
        return response;
    }
}
