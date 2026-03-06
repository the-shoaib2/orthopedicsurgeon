package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<Session> findByAccessTokenJti(UUID accessTokenJti);

    Optional<Session> findBySessionIdAndUserId(UUID sessionId, UUID userId);

    List<Session> findByUser(com.orthopedic.api.auth.entity.User user);

    List<Session> findAllByIsActiveTrue();
}
