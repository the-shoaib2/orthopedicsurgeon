package com.orthopedic.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "using_2fa")
    private boolean using2fa = false;

    @Column(name = "secret_2fa")
    private String secret2fa;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "lockout_until")
    private LocalDateTime lockoutUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.lockoutUntil = null;
    }

    public boolean isLocked() {
        return lockoutUntil != null && lockoutUntil.isAfter(LocalDateTime.now());
    }

    public void lock(int minutes) {
        this.lockoutUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    public String getUsername() {
        return email;
    }

    public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .collect(java.util.stream.Collectors.toList());
    }
}
