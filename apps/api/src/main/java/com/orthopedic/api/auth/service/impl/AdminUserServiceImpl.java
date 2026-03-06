package com.orthopedic.api.auth.service.impl;

import com.orthopedic.api.auth.dto.request.UserFilterRequest;
import com.orthopedic.api.auth.dto.response.UserDetailResponse;
import com.orthopedic.api.auth.dto.response.UserSummaryResponse;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.auth.service.AdminUserService;
import com.orthopedic.api.shared.dto.PageResponse;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<UserSummaryResponse> getAllUsers(UserFilterRequest filters, Pageable pageable) {
        Page<User> users;

        // Simple filtering for now. In a real app, use Specification for complex
        // filters
        if (filters.getQuery() != null && !filters.getQuery().isEmpty()) {
            users = userRepository
                    .findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                            filters.getQuery(), filters.getQuery(), filters.getQuery(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return PageResponse.fromPage(users.map(this::mapToSummary));
    }

    @Override
    public UserDetailResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDetail(user);
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "users", allEntries = true)
    public void lockUser(UUID id, int minutes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.lock(minutes);
        userRepository.save(user);
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "users", allEntries = true)
    public void unlockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.resetFailedAttempts();
        userRepository.save(user);
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "users", allEntries = true)
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "users", allEntries = true)
    public void resetPassword(UUID id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "users", allEntries = true)
    public void updateUserEnabledStatus(UUID id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    private UserSummaryResponse mapToSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRoles().stream().findFirst().orElse(null))
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserDetailResponse mapToDetail(User user) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRoles().stream().findFirst().orElse(null))
                .enabled(user.isEnabled())
                .using2fa(user.isUsing2fa())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLoginAt())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lockoutUntil(user.getLockoutUntil())
                .build();
    }
}
