package com.orthopedic.api.modules.patient.dto.request;

import com.orthopedic.api.modules.patient.entity.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdatePatientRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phone;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    private Patient.Gender gender;

    private Patient.BloodGroup bloodGroup;
    private String address;
    private String city;
    private String emergencyContactName;
    private String emergencyContactPhone;

    @NotNull(message = "Status is required")
    private Patient.PatientStatus status;
}
