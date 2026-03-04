package com.orthopedic.api.modules.doctor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateDoctorRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Hospital ID is required")
    private UUID hospitalId;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String bio;

    @NotNull(message = "Experience years is required")
    @Positive
    private Integer experienceYears;

    @NotNull(message = "Consultation fee is required")
    @Positive
    private BigDecimal consultationFee;

    private Boolean availableForOnline = true;

    private List<AvailabilityRequest> availabilities;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(UUID hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
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

    public List<AvailabilityRequest> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<AvailabilityRequest> availabilities) {
        this.availabilities = availabilities;
    }
}
