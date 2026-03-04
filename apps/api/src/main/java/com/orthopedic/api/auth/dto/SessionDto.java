package com.orthopedic.api.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SessionDto {
    private UUID sessionId;
    private String deviceName;
    private String deviceType;
    private String browser;
    private String os;
    private String ipAddress;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private boolean isActive;
    private boolean isCurrentSession;
}
