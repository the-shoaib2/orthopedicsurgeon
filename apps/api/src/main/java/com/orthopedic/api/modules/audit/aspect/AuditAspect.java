package com.orthopedic.api.modules.audit.aspect;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.modules.audit.annotation.LogMutation;
import com.orthopedic.api.modules.audit.dto.request.AuditEventRequest;
import com.orthopedic.api.modules.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
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
            String userAgent = "unknown";
            if (RequestContextHolder.getRequestAttributes() != null) {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                ip = request.getRemoteAddr();
                userAgent = request.getHeader("User-Agent");
            }

            AuditEventRequest event = AuditEventRequest.builder()
                    .action(annotation.action())
                    .userId(userId)
                    .ipAddress(ip)
                    .userAgent(userAgent)
                    .entityType(annotation.entityName())
                    .details(generateDetails(annotation.action(), annotation.entityName(), joinPoint))
                    .status("SUCCESS")
                    .build();

            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
        }
    }

    private String generateDetails(String action, String entity, JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: ").append(action);
        if (entity != null && !entity.isEmpty()) {
            sb.append(" on ").append(entity);
        }

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            sb.append(" | Arguments: ");
            for (int i = 0; i < args.length; i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(args[i]);
            }
        }
        return sb.toString();
    }

    private Method getMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
                .getParameterTypes();
        return joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
    }
}
