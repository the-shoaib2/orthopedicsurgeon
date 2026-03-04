package com.orthopedic.api.modules.booking.repository;

import com.orthopedic.api.modules.booking.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    List<AppointmentSlot> findByDoctorIdAndSlotDateAndIsBookedFalseOrderBySlotTimeAsc(UUID doctorId, LocalDate date);

    List<AppointmentSlot> findByDoctorIdAndSlotDateBetweenAndIsBookedFalseOrderBySlotDateAscSlotTimeAsc(
            UUID doctorId, LocalDate startDate, LocalDate endDate);
}
