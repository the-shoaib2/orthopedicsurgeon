package com.orthopedic.api.modules.patient.repository;

import com.orthopedic.api.modules.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

       @EntityGraph(attributePaths = { "user" })
       Optional<Patient> findByUserId(UUID userId);

       @EntityGraph(attributePaths = { "user" })
       @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
                     "(:bloodGroup IS NULL OR p.bloodGroup = :bloodGroup) AND " +
                     "(:gender IS NULL OR p.gender = :gender) AND " +
                     "(:city IS NULL OR p.city = :city) AND " +
                     "(:status IS NULL OR p.status = :status) AND " +
                     "(:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                     "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
       Page<Patient> findPatients(
                     @Param("bloodGroup") Patient.BloodGroup bloodGroup,
                     @Param("gender") Patient.Gender gender,
                     @Param("city") String city,
                     @Param("status") Patient.PatientStatus status,
                     @Param("search") String search,
                     Pageable pageable);
}
