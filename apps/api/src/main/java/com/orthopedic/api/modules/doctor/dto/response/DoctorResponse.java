package com.orthopedic.api.modules.doctor.dto.response;

import com.orthopedic.api.modules.doctor.entity.Doctor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed doctor profile response")
public class DoctorResponse {
    @Schema(description = "Unique identifier of the doctor", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Associated user basic information")
    private UserSummary user;

    @Schema(description = "Associated hospital basic information")
    private HospitalSummary hospital;

    @Schema(description = "Doctor's medical specialization", example = "Orthopedic Surgeon")
    private String specialization;

    @Schema(description = "Medical license number", example = "LIC-12345678")
    private String licenseNumber;

    @Schema(description = "Professional biography", example = "Specialist in joint replacement and trauma surgery.")
    private String bio;

    @Schema(description = "Years of professional experience", example = "15")
    private Integer experienceYears;

    @Schema(description = "Consultation fee for a single visit", example = "500.00")
    private BigDecimal consultationFee;

    @Schema(description = "Whether the doctor is available for online consultations", example = "true")
    private Boolean availableForOnline;

    @Schema(description = "Current status of the doctor profile", example = "ACTIVE")
    private Doctor.DoctorStatus status;

    @Schema(description = "List of weekly availability slots")
    private List<DoctorAvailabilityResponse> availabilities;

    @Schema(description = "Average rating from patient reviews", example = "4.8")
    private Double averageRating;

    @Schema(description = "Total number of completed appointments", example = "1250")
    private Integer totalAppointments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Basic user summary for doctor profile")
    public static class UserSummary {
        @Schema(description = "User ID", example = "1")
        private Long id;

        @Schema(description = "First name", example = "John")
        private String firstName;

        @Schema(description = "Last name", example = "Doe")
        private String lastName;

        @Schema(description = "Email address", example = "john.doe@example.com")
        private String email;

        @Schema(description = "Phone number", example = "+8801700000000")
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Basic hospital summary for doctor profile")
    public static class HospitalSummary {
        @Schema(description = "Hospital ID", example = "550e8400-e29b-41d4-a716-446655440001")
        private UUID id;

        @Schema(description = "Hospital name", example = "Central Orthopedic Hospital")
        private String name;

        @Schema(description = "City where hospital is located", example = "Dhaka")
        private String city;
    }
}
