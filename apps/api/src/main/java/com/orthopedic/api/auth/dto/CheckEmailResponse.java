package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckEmailResponse {
    @Schema(description = "Flag indicating if the email exists", example = "true")
    private boolean exists;

    @Schema(description = "Masked email for security", example = "s***b@o***c.com")
    private String mask;
}
