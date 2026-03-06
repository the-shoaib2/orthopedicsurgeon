package com.orthopedic.api.auth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AdminSessionDto extends SessionDto {
    private UUID userId;
    private String userEmail;
    private String userFullName;
    private String userRole;
}
