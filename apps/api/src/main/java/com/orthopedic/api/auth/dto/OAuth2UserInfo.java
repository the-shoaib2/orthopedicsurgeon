package com.orthopedic.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information about a user from an OAuth2 provider")
public class OAuth2UserInfo {

    @Schema(description = "Provider-specific unique ID", example = "10557234321")
    private String id;

    @Schema(description = "User's email from the provider", example = "shoaib@orthosync.com")
    private String email;

    @Schema(description = "User's full name from the provider", example = "Shoaib Hasan")
    private String name;

    @Schema(description = "URL of the user's profile image", example = "https://lh3.googleusercontent.com/...")
    private String imageUrl;

    @Schema(description = "OAuth2 provider name", example = "google")
    private String provider;
}
