package com.orthopedic.api.modules.hospital.service;

import com.orthopedic.api.modules.hospital.dto.request.CreateHospitalRequest;
import com.orthopedic.api.modules.hospital.dto.request.CreateServiceRequest;
import com.orthopedic.api.modules.hospital.dto.request.UpdateHospitalRequest;
import com.orthopedic.api.modules.hospital.dto.request.UpdateServiceRequest;
import com.orthopedic.api.modules.hospital.dto.response.HospitalResponse;
import com.orthopedic.api.modules.hospital.dto.response.ServiceResponse;
import com.orthopedic.api.modules.hospital.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HospitalService {
    Page<HospitalResponse> getAllHospitals(Hospital.HospitalStatus status, String city, Pageable pageable);

    HospitalResponse getHospitalById(UUID id);

    HospitalResponse createHospital(CreateHospitalRequest request);

    HospitalResponse updateHospital(UUID id, UpdateHospitalRequest request);

    void deleteHospital(UUID id);

    Page<ServiceResponse> getAllServices(ServiceEntity.ServiceStatus status, Pageable pageable);

    List<ServiceResponse> getServicesByHospital(UUID hospitalId);

    ServiceResponse createService(CreateServiceRequest request);

    ServiceResponse updateService(UUID serviceId, UpdateServiceRequest request);

    void deleteService(UUID serviceId);
}
