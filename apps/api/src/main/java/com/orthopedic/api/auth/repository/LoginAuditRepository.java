package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, java.util.UUID> {
}
