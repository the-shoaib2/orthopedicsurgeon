package com.orthopedic.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "access_token_jti", unique = true, nullable = false)
    private UUID accessTokenJti;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType; // DESKTOP, MOBILE, TABLET

    private String browser;

    private String os;

    @Column(name = "ip_address")
    private String ipAddress;

    private String location;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    @Column(name = "terminated_reason")
    private String terminatedReason;
}
