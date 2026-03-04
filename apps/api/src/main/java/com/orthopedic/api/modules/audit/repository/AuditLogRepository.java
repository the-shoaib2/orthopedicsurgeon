package com.orthopedic.api.modules.audit.repository;

import com.orthopedic.api.modules.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findAllByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    Page<AuditLog> findAllByUserId(UUID userId, Pageable pageable);
}
