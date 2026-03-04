package com.orthopedic.api.modules.doctor.dto.response;

import com.orthopedic.api.modules.doctor.entity.Doctor;
import java.math.BigDecimal;
import java.util.UUID;

public class DoctorSummaryResponse {
    private UUID id;
    private String fullName;
    private String specialization;
    private String hospitalName;
    private BigDecimal consultationFee;
    private Boolean availableForOnline;
    private Doctor.DoctorStatus status;
    private Boolean isFeatured;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Boolean getAvailableForOnline() {
        return availableForOnline;
    }

    public void setAvailableForOnline(Boolean availableForOnline) {
        this.availableForOnline = availableForOnline;
    }

    public Doctor.DoctorStatus getStatus() {
        return status;
    }

    public void setStatus(Doctor.DoctorStatus status) {
        this.status = status;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean featured) {
        isFeatured = featured;
    }
}
