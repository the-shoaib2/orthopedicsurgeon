package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.TrustedDeviceDto;
import com.orthopedic.api.auth.entity.User;

import java.util.List;
import java.util.UUID;

public interface TrustedDeviceService {

    /**
     * Get all trusted devices for a user.
     */
    List<TrustedDeviceDto> getTrustedDevices(User user);

    /**
     * Register a new device as trusted (needs MFA validation normally).
     */
    TrustedDeviceDto registerDevice(User user, String fingerprint, String name, String browser, String os,
            String ipAddress);

    /**
     * Check if a device fingerprint is trusted for the user.
     */
    boolean isDeviceTrusted(User user, String fingerprint);

    /**
     * Remove a device from trusted list.
     */
    void removeDevice(User user, UUID deviceId);
}
