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

    private final com.orthopedic.api.auth.repository.UserRepository userRepository;

    @Async
    public void logAudit(User user, String ip, String device, String status) {
        log.debug("Logging audit async: {} for user {}", status, user != null ? user.getEmail() : "anonymous");
        LoginAudit audit = new LoginAudit();
        audit.setUser(user);
        audit.setIpAddress(ip);
        audit.setDeviceInfo(device);
        audit.setStatus(status);
        loginAuditRepository.save(audit);
    }

    @Async
    public void logFailedLogin(String email, String ip, String device, String reason) {
        log.debug("Failed login from {}: {} - {}", email, ip, reason);
        User user = null;
        if (email != null) {
            user = userRepository.findByEmail(email).orElse(null);
        }
        logAudit(user, ip, device, "FAILURE (" + reason + ")");
    }

    @Async
    public void logSuccessfulLogin(String email, String ip, String device, String method) {
        log.debug("Successful login from {}: using {}", email, method);
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(user -> {
                logAudit(user, ip, device, "SUCCESS (" + method + ")");
            });
        }
    }
}
