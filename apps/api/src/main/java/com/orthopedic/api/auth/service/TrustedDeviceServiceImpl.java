package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.dto.TrustedDeviceDto;
import com.orthopedic.api.auth.entity.TrustedDevice;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.repository.TrustedDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrustedDeviceServiceImpl implements TrustedDeviceService {

    private final TrustedDeviceRepository trustedDeviceRepository;

    @Override
    public List<TrustedDeviceDto> getTrustedDevices(User user) {
        return trustedDeviceRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TrustedDeviceDto registerDevice(User user, String fingerprint, String name, String browser, String os,
            String ipAddress) {
        Optional<TrustedDevice> existing = trustedDeviceRepository.findByUserIdAndFingerprint(user.getId(),
                fingerprint);

        TrustedDevice device;
        if (existing.isPresent()) {
            device = existing.get();
            device.setLastSeen(LocalDateTime.now());
            // If already trusted, update name if needed
            if (name != null) {
                device.setName(name);
            }
        } else {
            device = TrustedDevice.builder()
                    .user(user)
                    .fingerprint(fingerprint)
                    .name(name != null ? name : "Unknown Device")
                    .browser(browser)
                    .os(os)
                    .firstSeen(LocalDateTime.now())
                    .lastSeen(LocalDateTime.now())
                    .isTrusted(true) // Set true upon explicit registration
                    .approvedAt(LocalDateTime.now())
                    .approvedFromIp(ipAddress)
                    .build();
        }

        return mapToDto(trustedDeviceRepository.save(device));
    }

    @Override
    public boolean isDeviceTrusted(User user, String fingerprint) {
        if (fingerprint == null || fingerprint.isEmpty()) {
            return false;
        }
        return trustedDeviceRepository.findByUserIdAndFingerprint(user.getId(), fingerprint)
                .map(TrustedDevice::isTrusted)
                .orElse(false);
    }

    @Override
    @Transactional
    public void removeDevice(User user, UUID deviceId) {
        TrustedDevice device = trustedDeviceRepository.findByDeviceIdAndUserId(deviceId, user.getId())
                .orElseThrow(() -> new AuthException("Device not found"));

        trustedDeviceRepository.delete(device);
    }

    private TrustedDeviceDto mapToDto(TrustedDevice device) {
        return TrustedDeviceDto.builder()
                .deviceId(device.getDeviceId())
                .name(device.getName())
                .browser(device.getBrowser())
                .os(device.getOs())
                .firstSeen(device.getFirstSeen())
                .lastSeen(device.getLastSeen())
                .isTrusted(device.isTrusted())
                .approvedAt(device.getApprovedAt())
                .approvedFromIp(device.getApprovedFromIp())
                .build();
    }
}
