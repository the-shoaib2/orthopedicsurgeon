package com.orthopedic.api.modules.doctor.repository;

import com.orthopedic.api.modules.doctor.entity.Doctor;
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
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

        @EntityGraph(attributePaths = { "user", "hospital" })
        Optional<Doctor> findByUserId(UUID userId);

        boolean existsByLicenseNumber(String licenseNumber);

        @EntityGraph(attributePaths = { "user", "hospital" })
        @Query("SELECT d FROM Doctor d JOIN d.hospital h WHERE " +
                        "(:specialization IS NULL OR d.specialization = :specialization) AND " +
                        "(:hospitalId IS NULL OR h.id = :hospitalId) AND " +
                        "(:city IS NULL OR h.city = :city) AND " +
                        "(:availableForOnline IS NULL OR d.availableForOnline = :availableForOnline) AND " +
                        "d.status = 'ACTIVE'")
        Page<Doctor> findAvailableDoctors(
                        @Param("specialization") String specialization,
                        @Param("hospitalId") UUID hospitalId,
                        @Param("city") String city,
                        @Param("availableForOnline") Boolean availableForOnline,
                        Pageable pageable);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'COMPLETED'")
        int countTotalAppointments(UUID doctorId);

        // Note: Average rating would come from a Review module which is not yet
        // implemented
}
