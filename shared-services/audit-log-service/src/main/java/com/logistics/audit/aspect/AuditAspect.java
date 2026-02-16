package com.logistics.audit.aspect;

import com.logistics.audit.model.AuditLog;
import com.logistics.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public Object auditWriteOperations(ProceedingJoinPoint joinPoint) throws Throwable {

        String action = determineAction(joinPoint);
        String userId = getCurrentUserId();
        String tenantId = getCurrentTenantId();
        HttpServletRequest request = getCurrentRequest();

        Object result = null;
        String status = "SUCCESS";
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw e;
        } finally {

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .tenantId(tenantId)
                    .action(action)
                    .entityType(extractEntityType(joinPoint))
                    .entityId(extractEntityId(result))
                    .ipAddress(request != null ? request.getRemoteAddr() : "UNKNOWN")
                    .userAgent(request != null ? request.getHeader("User-Agent") : "UNKNOWN")
                    .status(status)
                    .errorMessage(errorMessage)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogService.createAuditLog(auditLog);
            log.debug("Audit log created for action: {} by user: {}", action, userId);
        }
    }

    private String determineAction(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (method.isAnnotationPresent(PostMapping.class)) {
            return "CREATE";
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            return "UPDATE";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        } else if (method.isAnnotationPresent(PatchMapping.class)) {
            return "PATCH";
        }
        return "UNKNOWN";
    }

    private String extractEntityType(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className.replace("Controller", "").toUpperCase();
    }

    private String extractEntityId(Object result) {
        if (result == null) {
            return null;
        }
        // Try to extract ID from common response patterns
        try {
            if (result.getClass().getSimpleName().contains("ApiResponse")) {
                // Handle ApiResponse wrapper
                return "BULK_OPERATION";
            }
            return result.toString();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "ANONYMOUS";
    }

    private String getCurrentTenantId() {
        // Extract from security context or request header
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String tenantId = request.getHeader("X-Tenant-ID");
            if (tenantId != null) {
                return tenantId;
            }
        }
        return "DEFAULT";
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }
}
