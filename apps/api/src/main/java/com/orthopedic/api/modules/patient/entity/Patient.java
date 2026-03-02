package com.orthopedic.api.modules.patient.entity;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.shared.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patients_user_id", columnList = "user_id"),
    @Index(name = "idx_patients_status", columnList = "status"),
    @Index(name = "idx_patients_blood_group", columnList = "blood_group")
})
public class Patient extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String emergencyContactName;
    @Convert(converter = com.orthopedic.api.shared.converter.PiiEncryptionConverter.class)
    private String emergencyContactPhone;
    
    @Convert(converter = com.orthopedic.api.shared.converter.PiiEncryptionConverter.class)
    private String address;
    
    private String city;
    private String insuranceProvider;
    
    @Convert(converter = com.orthopedic.api.shared.converter.PiiEncryptionConverter.class)
    private String insuranceNumber;

    @Column(columnDefinition = "TEXT")
    private String medicalHistoryNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientStatus status = PatientStatus.ACTIVE;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientAllergy> allergies = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientMedicalCondition> conditions = new ArrayList<>();

    public enum BloodGroup {
        A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, O_POSITIVE, O_NEGATIVE
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum PatientStatus {
        ACTIVE, INACTIVE, DECEASED, SUSPENDED
    }
}
