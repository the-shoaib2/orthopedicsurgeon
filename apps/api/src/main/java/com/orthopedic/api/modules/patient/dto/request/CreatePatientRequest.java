package com.orthopedic.api.modules.patient.dto.request;

import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.entity.PatientAllergy;
import com.orthopedic.api.modules.patient.entity.PatientMedicalCondition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreatePatientRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    private Patient.BloodGroup bloodGroup;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Patient.Gender gender;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String address;
    private String city;
    private String insuranceProvider;
    private String insuranceNumber;
    private String medicalHistoryNotes;

    private List<AllergyRequest> allergies;
    private List<ConditionRequest> conditions;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Patient.BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(Patient.BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Patient.Gender getGender() {
        return gender;
    }

    public void setGender(Patient.Gender gender) {
        this.gender = gender;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getMedicalHistoryNotes() {
        return medicalHistoryNotes;
    }

    public void setMedicalHistoryNotes(String medicalHistoryNotes) {
        this.medicalHistoryNotes = medicalHistoryNotes;
    }

    public List<AllergyRequest> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyRequest> allergies) {
        this.allergies = allergies;
    }

    public List<ConditionRequest> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionRequest> conditions) {
        this.conditions = conditions;
    }

    public static class AllergyRequest {
        private String allergy;
        private PatientAllergy.Severity severity;

        public String getAllergy() {
            return allergy;
        }

        public void setAllergy(String allergy) {
            this.allergy = allergy;
        }

        public PatientAllergy.Severity getSeverity() {
            return severity;
        }

        public void setSeverity(PatientAllergy.Severity severity) {
            this.severity = severity;
        }
    }

    public static class ConditionRequest {
        private String condition;
        private LocalDate diagnosedDate;
        private Boolean isActive = true;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public LocalDate getDiagnosedDate() {
            return diagnosedDate;
        }

        public void setDiagnosedDate(LocalDate diagnosedDate) {
            this.diagnosedDate = diagnosedDate;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }
}
