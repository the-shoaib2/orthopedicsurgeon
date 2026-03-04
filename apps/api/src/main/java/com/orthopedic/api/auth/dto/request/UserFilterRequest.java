package com.orthopedic.api.auth.dto.request;

import com.orthopedic.api.auth.entity.Role;
import lombok.Data;

@Data
public class UserFilterRequest {
    private String query;
    private Role role;
    private Boolean enabled;
}
