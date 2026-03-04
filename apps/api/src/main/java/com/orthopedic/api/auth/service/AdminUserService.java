package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.request.UserFilterRequest;
import com.orthopedic.api.auth.dto.response.UserDetailResponse;
import com.orthopedic.api.auth.dto.response.UserSummaryResponse;
import com.orthopedic.api.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminUserService {
    PageResponse<UserSummaryResponse> getAllUsers(UserFilterRequest filters, Pageable pageable);

    UserDetailResponse getUserById(UUID id);

    void lockUser(UUID id, int minutes);

    void unlockUser(UUID id);

    void deleteUser(UUID id);

    void resetPassword(UUID id, String newPassword);

    void updateUserEnabledStatus(UUID id, boolean enabled);
}
