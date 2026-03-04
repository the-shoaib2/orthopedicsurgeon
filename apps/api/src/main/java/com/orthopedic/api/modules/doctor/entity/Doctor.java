package com.orthopedic.api.modules.doctor.entity;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.hospital.entity.Hospital;
import com.orthopedic.api.shared.base.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors", indexes = {
        @Index(name = "idx_doctors_hospital_id", columnList = "hospital_id"),
        @Index(name = "idx_doctors_status", columnList = "status"),
        @Index(name = "idx_doctors_specialization", columnList = "specialization")
})
public class Doctor extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal consultationFee;

    @Column(nullable = false)
    private Boolean availableForOnline = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DoctorStatus status = DoctorStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean isFeatured = false;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorAvailability> availabilities = new ArrayList<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
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

    public DoctorStatus getStatus() {
        return status;
    }

    public void setStatus(DoctorStatus status) {
        this.status = status;
    }

    public List<DoctorAvailability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<DoctorAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean featured) {
        isFeatured = featured;
    }

    public enum DoctorStatus {
        ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED
    }
}
