package com.orthopedic.api.modules.admin.service;

import com.orthopedic.api.auth.entity.Role;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.repository.RoleRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.orthopedic.api.modules.admin.dto.UserDto;
import com.orthopedic.api.modules.audit.annotation.LogMutation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Page<UserDto> getUsers(Pageable pageable, String search) {
        Page<User> usersPage;
        if (search != null && !search.isEmpty()) {
            usersPage = userRepository
                    .findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                            search, search, search, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        return usersPage.map(this::mapToDto);
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));
        return mapToDto(user);
    }

    @Transactional
    @LogMutation(action = "UPDATE_USER_ROLES", entityName = "USER")
    public UserDto updateUserRoles(UUID id, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        if (roleNames == null || roleNames.isEmpty()) {
            throw new AuthException("Roles cannot be empty");
        }

        Set<Role> newRoles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new AuthException("Role not found: " + roleName));
            newRoles.add(role);
        }

        user.setRoles(newRoles);
        return mapToDto(userRepository.save(user));
    }

    @Transactional
    @LogMutation(action = "UPDATE_USER_STATUS", entityName = "USER")
    public UserDto updateUserStatus(UUID id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        user.setEnabled(enabled);
        return mapToDto(userRepository.save(user));
    }

    @Transactional
    @LogMutation(action = "UNLOCK_USER", entityName = "USER")
    public UserDto unlockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));

        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        return mapToDto(userRepository.save(user));
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .using2fa(user.isUsing2fa())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .isLocked(user.isLocked())
                .lockoutUntil(user.getLockoutUntil())
                .build();
    }
}
