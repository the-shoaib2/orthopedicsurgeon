package com.orthopedic.api.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrustedDeviceDto {
    private UUID deviceId;
    private String name;
    private String browser;
    private String os;
    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private boolean isTrusted;
    private LocalDateTime approvedAt;
    private String approvedFromIp;
}
