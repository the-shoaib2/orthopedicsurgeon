package com.orthopedic.api.modules.website.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSiteSettingRequest {
    @NotBlank(message = "Value is required")
    private String value;
}
