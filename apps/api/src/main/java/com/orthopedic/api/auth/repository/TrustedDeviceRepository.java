package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, UUID> {
    List<TrustedDevice> findByUserId(UUID userId);

    Optional<TrustedDevice> findByUserIdAndFingerprint(UUID userId, String fingerprint);

    Optional<TrustedDevice> findByDeviceIdAndUserId(UUID deviceId, UUID userId);
}
