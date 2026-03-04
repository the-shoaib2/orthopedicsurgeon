package com.orthopedic.api.auth.dto;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for user registration")
public class RegisterResponse {

    @Schema(description = "Newly created user ID", example = "101")
    private UUID userId;

    @Schema(description = "Registered email address", example = "shoaib@orthosync.com")
    private String email;

    @Schema(description = "Success message", example = "User registered successfully")
    private String message;
}
