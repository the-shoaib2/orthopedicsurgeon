package com.orthopedic.api.modules.appointment.repository;

import com.orthopedic.api.modules.appointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

        @EntityGraph(attributePaths = { "doctor", "patient", "hospital", "service" })
        @Query("SELECT a FROM Appointment a WHERE " +
                        "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
                        "(:patientId IS NULL OR a.patient.id = :patientId) AND " +
                        "(:hospitalId IS NULL OR a.hospital.id = :hospitalId) AND " +
                        "(:status IS NULL OR a.status = :status) AND " +
                        "(:type IS NULL OR a.type = :type) AND " +
                        "(:dateFrom IS NULL OR a.appointmentDate >= :dateFrom) AND " +
                        "(:dateTo IS NULL OR a.appointmentDate <= :dateTo)")
        Page<Appointment> findAppointments(
                        @Param("doctorId") UUID doctorId,
                        @Param("patientId") UUID patientId,
                        @Param("hospitalId") UUID hospitalId,
                        @Param("status") Appointment.AppointmentStatus status,
                        @Param("type") Appointment.AppointmentType type,
                        @Param("dateFrom") LocalDate dateFrom,
                        @Param("dateTo") LocalDate dateTo,
                        Pageable pageable);

        @EntityGraph(attributePaths = { "doctor" })
        @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date " +
                        "AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
        List<Appointment> findOccupiedSlots(@Param("doctorId") UUID doctorId, @Param("date") LocalDate date);

        boolean existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
                        UUID doctorId, LocalDate date, LocalTime startTime,
                        List<Appointment.AppointmentStatus> statuses);

        @Query("SELECT a.status as status, COUNT(a) as count FROM Appointment a WHERE " +
                        "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
                        "(:patientId IS NULL OR a.patient.id = :patientId) " +
                        "GROUP BY a.status")
        List<Object[]> countAppointmentsByStatus(
                        @Param("doctorId") UUID doctorId,
                        @Param("patientId") UUID patientId);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
                        "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
                        "(:patientId IS NULL OR a.patient.id = :patientId) AND " +
                        "(:hospitalId IS NULL OR a.hospital.id = :hospitalId) AND " +
                        "(:status IS NULL OR a.status = :status) AND " +
                        "(:type IS NULL OR a.type = :type) AND " +
                        "(:dateFrom IS NULL OR a.appointmentDate >= :dateFrom) AND " +
                        "(:dateTo IS NULL OR a.appointmentDate <= :dateTo)")
        long countByFilters(
                        @Param("doctorId") UUID doctorId,
                        @Param("patientId") UUID patientId,
                        @Param("hospitalId") UUID hospitalId,
                        @Param("status") Appointment.AppointmentStatus status,
                        @Param("type") Appointment.AppointmentType type,
                        @Param("dateFrom") LocalDate dateFrom,
                        @Param("dateTo") LocalDate dateTo);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
        long countByStatus(@Param("status") Appointment.AppointmentStatus status);

        @EntityGraph(attributePaths = { "doctor", "patient" })
        List<Appointment> findAllByAppointmentDateAndStatus(java.time.LocalDate date,
                        Appointment.AppointmentStatus status);
}
