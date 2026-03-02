package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.entity.LoginAudit;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.LoginAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final LoginAuditRepository loginAuditRepository;

    @Async
    public void logAudit(User user, String ip, String device, String status) {
        log.debug("Logging audit async: {} for user {}", status, user.getEmail());
        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setIpAddress(ip);
        audit.setDeviceInfo(device);
        audit.setStatus(status);
        loginAuditRepository.save(audit);
    }
}
