package com.orthopedic.api.auth.dto;

import com.orthopedic.api.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private boolean isTwoFactorEnabled;
    private String status;
    private String imageUrl;
    private java.time.LocalDateTime createdAt;
    private Set<String> roles;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .isTwoFactorEnabled(user.isUsing2fa())
                .status(user.isEnabled() ? "ACTIVE" : "INACTIVE")
                .imageUrl(null) // Not currently stored in User entity
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
