package com.orthopedic.api.modules.prescription.repository;

import com.orthopedic.api.modules.prescription.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    @EntityGraph(attributePaths = { "appointment", "patient", "doctor" })
    Optional<Prescription> findByAppointmentId(UUID appointmentId);

    @EntityGraph(attributePaths = { "appointment", "patient", "doctor" })
    Page<Prescription> findAllByPatientId(UUID patientId, Pageable pageable);

    @EntityGraph(attributePaths = { "appointment", "patient", "doctor" })
    Page<Prescription> findAllByDoctorId(UUID doctorId, Pageable pageable);
}
