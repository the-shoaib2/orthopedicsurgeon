package com.orthopedic.api.modules.patient.dto.response;

import com.orthopedic.api.modules.patient.entity.Patient;
import com.orthopedic.api.modules.patient.entity.PatientAllergy;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PatientResponse {
    private UUID id;
    private UserSummary user;
    private Patient.BloodGroup bloodGroup;
    private LocalDate dateOfBirth;
    private int age;
    private Patient.Gender gender;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String address;
    private String city;
    private String insuranceProvider;
    private String insuranceNumber;
    private String medicalHistoryNotes;
    private Patient.PatientStatus status;
    private List<AllergyResponse> allergies;
    private List<ConditionResponse> conditions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserSummary getUser() {
        return user;
    }

    public void setUser(UserSummary user) {
        this.user = user;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public Patient.PatientStatus getStatus() {
        return status;
    }

    public void setStatus(Patient.PatientStatus status) {
        this.status = status;
    }

    public List<AllergyResponse> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergyResponse> allergies) {
        this.allergies = allergies;
    }

    public List<ConditionResponse> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionResponse> conditions) {
        this.conditions = conditions;
    }

    public static class UserSummary {
        private UUID id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class AllergyResponse {
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

    public static class ConditionResponse {
        private String condition;
        private LocalDate diagnosedDate;
        private Boolean isActive;

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
