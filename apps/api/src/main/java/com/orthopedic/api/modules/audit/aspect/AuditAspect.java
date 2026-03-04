package com.orthopedic.api.modules.audit.aspect;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.audit.annotation.LogMutation;
import com.orthopedic.api.modules.audit.dto.request.AuditEventRequest;
import com.orthopedic.api.modules.audit.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuditAspect.class);
    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(value = "@annotation(com.orthopedic.api.modules.audit.annotation.LogMutation)", returning = "result")
    public void logMutation(JoinPoint joinPoint, Object result) {
        try {
            Method method = getMethod(joinPoint);
            LogMutation annotation = method.getAnnotation(LogMutation.class);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            java.util.UUID userId = (auth != null && auth.getPrincipal() instanceof User)
                    ? ((User) auth.getPrincipal()).getId()
                    : null;

            String ip = "unknown";
            if (RequestContextHolder.getRequestAttributes() != null) {
                ip = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest().getRemoteAddr();
            }

            AuditEventRequest event = AuditEventRequest.builder()
                    .action(annotation.action())
                    .userId(userId)
                    .ipAddress(ip)
                    .entityType(annotation.entityName())
                    // In a more complex scenario, we'd extract the ID from the result or arguments
                    .details("Action: " + annotation.action() + " performed on " + annotation.entityName())
                    .build();

            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
        }
    }

    private Method getMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
                .getParameterTypes();
        return joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
    }
}
